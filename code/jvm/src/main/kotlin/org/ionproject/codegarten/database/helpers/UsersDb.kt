package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.Classroom
import org.ionproject.codegarten.database.dto.DtoListWrapper
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserAssignment
import org.ionproject.codegarten.database.dto.UserClassroom
import org.ionproject.codegarten.database.dto.UserClassroomDto
import org.ionproject.codegarten.database.dto.UserClassroomMembership
import org.ionproject.codegarten.database.dto.UserClassroomMembership.NOT_A_MEMBER
import org.ionproject.codegarten.exceptions.NotFoundException
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

private const val GET_USER_BASE = "SELECT uid, name, gh_id, gh_token FROM USERS"
private const val GET_USERS_BASE = "SELECT uid, name, gh_id, gh_token, COUNT(*) OVER() as count FROM USERS"

private const val GET_USER_QUERY = "$GET_USER_BASE WHERE uid = :userId"
private const val GET_USER_BY_GITHUB_ID_QUERY = "$GET_USER_BASE WHERE gh_id = :gitHubId"

private const val CREATE_USER_QUERY =
    "INSERT INTO USERS(name, gh_id, gh_token) VALUES (:name, :ghId, :ghToken)"

private const val INSERT_USER_IN_CLASSROOM_QUERY = "INSERT INTO USER_CLASSROOM VALUES(:role, :userId, :classroomId)"

private const val UPDATE_USER_START = "UPDATE USERS SET"
private const val UPDATE_USER_END = "WHERE uid = :userId"

private const val DELETE_USER_QUERY = "DELETE FROM USERS WHERE uid = :userId"

// Classrooms
private const val GET_USER_CLASSROOM_BASE = "SELECT uid, name, gh_id, gh_token, classroom_role, classroom_id FROM V_USER_CLASSROOM"
private const val GET_USERS_CLASSROOM_BASE = "SELECT uid, name, gh_id, gh_token, classroom_role, classroom_id, COUNT(*) OVER() as count FROM V_USER_CLASSROOM"

private const val GET_USERS_IN_CLASSROOM_QUERY =
    "$GET_USERS_CLASSROOM_BASE WHERE classroom_id = :classroomId ORDER BY classroom_role DESC, uid"
private const val SEARCH_USERS_IN_CLASSROOM_QUERY =
    "$GET_USERS_CLASSROOM_BASE WHERE classroom_id = :classroomId AND SIMILARITY(name, :search) > 0 ORDER BY SIMILARITY(name, :search) DESC"

private const val GET_USER_IN_CLASSROOM_QUERY =
    "SELECT uid from USER_CLASSROOM where cid = :classroomId AND uid = :userId"
private const val GET_TEACHERS_IN_CLASSROOM_COUNT =
    "SELECT COUNT(uid) as count FROM USER_CLASSROOM where cid = :classroomId AND type = 'teacher'"

private const val ADD_USER_TO_CLASSROOM_QUERY =
    "INSERT INTO USER_CLASSROOM VALUES(:role, :userId, :classroomId)"
private const val UPDATE_USER_IN_CLASSROOM_START = "UPDATE USER_CLASSROOM SET"
private const val UPDATE_USER_IN_CLASSROOM_END = "WHERE uid = :userId"
private const val DELETE_USER_FROM_CLASSROOM_QUERY = "DELETE FROM USER_CLASSROOM WHERE uid = :userId AND cid = :classroomId"

private const val GET_USER_CLASSROOM_QUERY =
    "$GET_USER_CLASSROOM_BASE WHERE uid = :userId AND classroom_id = :classroomId"

// Assignments
private const val GET_USER_ASSIGNMENT_BASE = "SELECT uid, name, gh_id, gh_token, repo_id, assignment_id FROM V_USER_ASSIGNMENT"
private const val GET_USERS_ASSIGNMENT_BASE = "SELECT uid, name, gh_id, gh_token, repo_id, assignment_id, COUNT(*) OVER() as count FROM V_USER_ASSIGNMENT"

private const val GET_USERS_IN_ASSIGNMENT_QUERY =
    "$GET_USERS_ASSIGNMENT_BASE WHERE assignment_id = :assignmentId ORDER BY uid"

private const val GET_USER_ASSIGNMENT_QUERY =
    "$GET_USER_ASSIGNMENT_BASE WHERE assignment_id = :assignmentId AND uid = :userId"

private const val GET_USER_ID_IN_ASSIGNMENT = "SELECT uid from USER_ASSIGNMENT where aid = :assignmentId AND uid = :userId"

private const val ADD_USER_TO_ASSIGNMENT_QUERY =
    "INSERT INTO USER_ASSIGNMENT VALUES(:userId, :assignmentId, :repoId) ON CONFLICT (uid, aid) DO UPDATE SET repo_id = :repoId"
private const val DELETE_USER_FROM_ASSIGNMENT_QUERY = "DELETE FROM USER_ASSIGNMENT WHERE uid = :userId AND aid = :assignmentId"

// Teams
private const val GET_USERS_OF_TEAM_QUERY = "$GET_USERS_BASE WHERE uid IN (SELECT uid FROM USER_TEAM WHERE tid = :teamId)"

private const val ADD_USER_TO_TEAM_QUERY = "INSERT INTO USER_TEAM(tid, uid) VALUES(:teamId, :userId)"
private const val DELETE_USER_FROM_TEAM_QUERY = "DELETE FROM USER_TEAM WHERE tid = :teamId AND uid = :userId"

@Component
class UsersDb(
    val classroomsDb: ClassroomsDb,
    val assignmentsDb: AssignmentsDb,
    val teamsDb: TeamsDb,
    val jdbi: Jdbi
) {

    fun getUserById(userId: Int) = jdbi.getOne(GET_USER_QUERY, User::class.java, mapOf("userId" to userId))

    fun getUserByGitHubId(gitHubId: Int) =
        jdbi.getOne(
            GET_USER_BY_GITHUB_ID_QUERY, User::class.java,
            mapOf(
                "gitHubId" to gitHubId
            )
        )

    fun createOrUpdateUser(name: String, ghId: Int, ghToken: String): Int {
        try {
            val user = getUserByGitHubId(ghId)
            editUser(user.uid, gitHubToken = ghToken)

            return user.uid
        } catch (ex: NotFoundException) {
            return createUser(name, ghId, ghToken)
        }
    }

    fun createUser(name: String, ghId: Int, ghToken: String) =
        jdbi.insertAndGetGeneratedKey(
            CREATE_USER_QUERY, Int::class.java,
            mapOf(
                "name" to name,
                "ghId" to ghId,
                "ghToken" to ghToken
            ),
        )

    fun editUser(userId: Int, name: String? = null, gitHubToken: String? = null) {
        if (name == null && gitHubToken == null) {
            return
        }

        val updateFields = mutableMapOf<String, Any>()
        if (name != null) updateFields["name"] = name
        if (gitHubToken != null) updateFields["gh_token"] = gitHubToken

        jdbi.update(
            UPDATE_USER_START,
            updateFields,
            UPDATE_USER_END,
            mapOf("userId" to userId)
        )
    }

    fun deleteUser(userId: Int) {
        jdbi.delete(
            DELETE_USER_QUERY,
            mapOf("userId" to userId)
        )
    }

    // Classrooms

    fun getUsersInClassroom(orgId: Int, classroomNumber: Int, page: Int, limit: Int): DtoListWrapper<UserClassroomDto> {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        val results = jdbi.getList(
            GET_USERS_IN_CLASSROOM_QUERY,
            UserClassroomDto::class.java, page, limit,
            mapOf("classroomId" to classroomId)
        )

        return DtoListWrapper(
            count = if (results.isEmpty()) 0 else results[0].count!!,
            results = results
        )
    }

    fun searchUsersInClassroom(orgId: Int, classroomNumber: Int, search: String, page: Int, limit: Int): DtoListWrapper<UserClassroomDto> {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        val results = jdbi.getList(
            SEARCH_USERS_IN_CLASSROOM_QUERY,
            UserClassroomDto::class.java, page, limit,
            mapOf(
                "classroomId" to classroomId,
                "search" to search
            )
        )

        return DtoListWrapper(
            count = if (results.isEmpty()) 0 else results[0].count!!,
            results = results
        )
    }

    fun getTeachersInClassroomCount(classroomId: Int) =
        jdbi.getOne(
            GET_TEACHERS_IN_CLASSROOM_COUNT,
            Int::class.java,
            mapOf("classroomId" to classroomId)
        )

    fun addOrEditUserInClassroom(orgId: Int, classroomNumber: Int, userId: Int, role: String) {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid

        addOrEditUserInClassroom(classroomId, userId, role)
    }

    fun addOrEditUserInClassroom(classroomId: Int, userId: Int, role: String) {
        val maybeUserId = jdbi.tryGetOne(
            GET_USER_IN_CLASSROOM_QUERY, Int::class.java,
            mapOf("classroomId" to classroomId, "userId" to userId)
        )

        if (maybeUserId.isEmpty) {
            jdbi.insert(
                ADD_USER_TO_CLASSROOM_QUERY,
                mapOf(
                    "role" to role,
                    "userId" to userId,
                    "classroomId" to classroomId
                )
            )
        } else {
            jdbi.update(
                UPDATE_USER_IN_CLASSROOM_START,
                mapOf("type" to role),
                UPDATE_USER_IN_CLASSROOM_END,
                mapOf("userId" to userId)
            )
        }
    }

    fun deleteUserFromClassroom(orgId: Int, classroomNumber: Int, userId: Int) {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid

        jdbi.delete(
            DELETE_USER_FROM_CLASSROOM_QUERY,
            mapOf(
                "userId" to userId,
                "classroomId" to classroomId
            )
        )
    }

    fun getUserMembershipInClassroom(orgId: Int, classroomNumber: Int, userId: Int): UserClassroom {
        val classroom = classroomsDb.getClassroomByNumber(orgId, classroomNumber)
        return getUserMembershipInClassroom(classroom, userId)
    }

    fun getUserMembershipInClassroom(classroomId: Int, userId: Int): UserClassroom {
        val classroom = classroomsDb.getClassroomById(classroomId)
        return getUserMembershipInClassroom(classroom, userId)
    }

    private fun getUserMembershipInClassroom(classroom: Classroom, userId: Int): UserClassroom {
        val maybeUserClassroom = jdbi.tryGetOne(
            GET_USER_CLASSROOM_QUERY,
            UserClassroomDto::class.java,
            mapOf(
                "userId" to userId,
                "classroomId" to classroom.cid
            )
        )

        val userClassroom = if (maybeUserClassroom.isPresent) maybeUserClassroom.get() else null

        return UserClassroom(
            role = if (userClassroom == null) NOT_A_MEMBER else UserClassroomMembership.valueOf(maybeUserClassroom.get().classroom_role.toUpperCase()),
            classroom = classroom,
            user = if (userClassroom == null) null else User(userClassroom.uid, userClassroom.name, userClassroom.gh_id, userClassroom.gh_token)
        )
    }

    fun addUserToClassroom(classroomId: Int, userId: Int, role: UserClassroomMembership) {
        jdbi.insert(
            INSERT_USER_IN_CLASSROOM_QUERY,
            mapOf(
                "role" to role.name.toLowerCase(),
                "userId" to userId,
                "classroomId" to classroomId
            )
        )
    }

    fun addUserToClassroom(orgId: Int, classroomNumber: Int, userId: Int, role: UserClassroomMembership) {
        addUserToClassroom(
            classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid,
            userId,
            role
        )
    }

    // Assignments

    fun getUsersInAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, page: Int, limit: Int): DtoListWrapper<UserAssignment> {
        val assignmentId = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid
        val results = jdbi.getList(
            GET_USERS_IN_ASSIGNMENT_QUERY,
            UserAssignment::class.java, page, limit,
            mapOf("assignmentId" to assignmentId)
        )

        return DtoListWrapper(
            count = if (results.isEmpty()) 0 else results[0].count!!,
            results = results
        )
    }

    fun getUserAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, userId: Int): UserAssignment {
        val assignmentId = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid
        return getUserAssignment(assignmentId, userId)
    }

    fun getUserAssignment(assignmentId: Int, userId: Int) =
        jdbi.getOne(
            GET_USER_ASSIGNMENT_QUERY,
            UserAssignment::class.java,
            mapOf("userId" to userId, "assignmentId" to assignmentId)
        )

    fun addUserToAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, userId: Int, repoId: Int) {
        val assignmentId = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid

        jdbi.insert(
            ADD_USER_TO_ASSIGNMENT_QUERY,
            mapOf(
                "userId" to userId,
                "assignmentId" to assignmentId,
                "repoId" to repoId
            )
        )
    }

    fun deleteUserFromAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, userId: Int) {
        val assignmentId = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid

        jdbi.delete(
            DELETE_USER_FROM_ASSIGNMENT_QUERY,
            mapOf(
                "userId" to userId,
                "assignmentId" to assignmentId
            )
        )
    }

    fun isUserInAssignment(assignmentId: Int, userId: Int): Boolean {
        val userMembership = jdbi.tryGetOne(
            GET_USER_ID_IN_ASSIGNMENT,
            Int::class.java,
            mapOf(
                "assignmentId" to assignmentId,
                "userId" to userId,
            )
        )

        return userMembership.isPresent
    }

    // Teams
    fun getUsersInTeam(orgId: Int, classroomNumber: Int, teamNumber: Int, page: Int, limit: Int): DtoListWrapper<User> {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        return getUsersInTeam(classroomId, teamNumber, page, limit)
    }

    fun getUsersInTeam(classroomId: Int, teamNumber: Int, page: Int, limit: Int): DtoListWrapper<User> {
        val teamId = teamsDb.getTeam(classroomId, teamNumber).tid
        return getUsersInTeam(teamId, page, limit)
    }

    fun getUsersInTeam(teamId: Int, page: Int, limit: Int): DtoListWrapper<User> {
        val results = jdbi.getList(
            GET_USERS_OF_TEAM_QUERY, User::class.java,
            page, limit,
            mapOf("teamId" to teamId)
        )

        return DtoListWrapper(
            count = if (results.isEmpty()) 0 else results[0].count!!,
            results = results
        )
    }

    fun addUserToTeam(orgId: Int, classroomNumber: Int, teamNumber: Int, userId: Int) {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        addUserToTeam(classroomId, teamNumber, userId)
    }

    fun addUserToTeam(classroomId: Int, teamNumber: Int, userId: Int) {
        val teamId = teamsDb.getTeam(classroomId, teamNumber).tid
        addUserToTeam(teamId, userId)
    }

    fun addUserToTeam(teamId: Int, userId: Int) =
        jdbi.insert(
            ADD_USER_TO_TEAM_QUERY,
            mapOf(
                "teamId" to teamId,
                "userId" to userId
            )
        )

    fun deleteUserFromTeam(orgId: Int, classroomNumber: Int, teamNumber: Int, userId: Int) {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        deleteUserFromTeam(classroomId, teamNumber, userId)
    }

    fun deleteUserFromTeam(classroomId: Int, teamNumber: Int, userId: Int) {
        val teamId = teamsDb.getTeam(classroomId, teamNumber).tid
        deleteUserFromTeam(teamId, userId)
    }

    fun deleteUserFromTeam(teamId: Int, userId: Int) =
        jdbi.delete(
            DELETE_USER_FROM_TEAM_QUERY,
            mapOf(
                "teamId" to teamId,
                "userId" to userId
            )
        )
}