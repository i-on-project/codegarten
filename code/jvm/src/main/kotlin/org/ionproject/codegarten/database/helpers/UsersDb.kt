package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.Assignment
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserAssignment
import org.ionproject.codegarten.database.dto.UserClassroom
import org.ionproject.codegarten.database.dto.UserClassroomDto
import org.ionproject.codegarten.database.dto.UserClassroomMembership
import org.ionproject.codegarten.database.dto.UserClassroomMembership.NOT_A_MEMBER
import org.ionproject.codegarten.exceptions.NotFoundException
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component
import java.util.*

private const val GET_USERS_BASE = "SELECT uid, name, gh_id, gh_token FROM USERS"
private const val GET_USER_QUERY = "$GET_USERS_BASE WHERE uid = :userId"
private const val GET_USER_BY_GITHUB_ID_QUERY = "$GET_USERS_BASE WHERE gh_id = :gitHubId"

private const val CREATE_USER_QUERY =
    "INSERT INTO USERS(name, gh_id, gh_token) VALUES (:name, :ghId, :ghToken)"

private const val INSERT_USER_IN_CLASSROOM_QUERY = "INSERT INTO USER_CLASSROOM VALUES(:role, :userId, :classroomId)"

private const val UPDATE_USER_START = "UPDATE USERS SET"
private const val UPDATE_USER_END = "WHERE uid = :userId"

private const val DELETE_USER_QUERY = "DELETE FROM USERS WHERE uid = :userId"


private const val GET_USERS_IN_CLASSROOM_QUERY =
    "SELECT uid, name, gh_id, gh_token, classroom_role, classroom_id FROM V_USER_CLASSROOM WHERE classroom_id = :classroomId ORDER BY uid"
private const val GET_USER_IN_CLASSROOM_QUERY =
    "SELECT uid from USER_CLASSROOM where cid = :classroomId AND uid = :userId"
private const val GET_USERS_IN_CLASSROOM_COUNT =
    "SELECT COUNT(uid) as count FROM USER_CLASSROOM where cid IN " +
        "(SELECT cid FROM CLASSROOM WHERE org_id = :orgId AND number = :classroomNumber)"

private const val ADD_USER_TO_CLASSROOM_QUERY =
    "INSERT INTO USER_CLASSROOM VALUES(:role, :userId, :classroomId)"
private const val UPDATE_USER_IN_CLASSROOM_START = "UPDATE USER_CLASSROOM SET"
private const val UPDATE_USER_IN_CLASSROOM_END = "WHERE uid = :userId"
private const val DELETE_USER_FROM_CLASSROOM_QUERY = "DELETE FROM USER_CLASSROOM WHERE uid = :userId AND cid = :classroomId"

private const val GET_USER_CLASSROOM_BASE = "SELECT uid, name, gh_id, gh_token, classroom_role, classroom_id FROM V_USER_CLASSROOM"
private const val GET_USER_ASSIGNMENT_BASE = "SELECT uid, name, gh_id, gh_token, repo_id, assignment_id FROM V_USER_ASSIGNMENT"

private const val GET_USER_CLASSROOM_QUERY =
    "$GET_USER_CLASSROOM_BASE WHERE uid = :userId AND classroom_id = :classroomId"

private const val GET_USERS_IN_ASSIGNMENT_QUERY =
    "$GET_USER_ASSIGNMENT_BASE WHERE assignment_id = :assignmentId ORDER BY uid"
private const val GET_USERS_IN_ASSIGNMENT_COUNT =
    "SELECT COUNT(uid) as count FROM USER_ASSIGNMENT where aid IN " +
        "(SELECT aid FROM V_ASSIGNMENT WHERE org_id = :orgId AND " +
        "classroom_number = :classroomNumber AND number = :assignmentNumber)"

private const val GET_USER_ASSIGNMENT_QUERY =
    "$GET_USER_ASSIGNMENT_BASE WHERE assignment_id = :assignmentId AND uid = :userId"

private const val GET_USER_ID_IN_ASSIGNMENT = "SELECT uid from USER_ASSIGNMENT where aid = :assignmentId"

private const val ADD_USER_TO_ASSIGNMENT_QUERY =
    "INSERT INTO USER_ASSIGNMENT VALUES(:userId, :assignmentId, :repoId) ON CONFLICT (uid, aid) DO UPDATE SET repo_id = :repoId"
private const val DELETE_USER_FROM_ASSIGNMENT_QUERY = "DELETE FROM USER_ASSIGNMENT WHERE uid = :userId AND aid = :assignmentId"

@Component
class UsersDb(
    val classroomsDb: ClassroomsDb,
    val assignmentsDb: AssignmentsDb,
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

    fun getUsersInClassroom(orgId: Int, classroomNumber: Int, page: Int, perPage: Int): List<UserClassroomDto> {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        return jdbi.getList(
            GET_USERS_IN_CLASSROOM_QUERY,
            UserClassroomDto::class.java, page, perPage,
            mapOf("classroomId" to classroomId)
        )
    }
    fun getUsersInClassroomCount(orgId: Int, classroomNumber: Int) =
        jdbi.getOne(
            GET_USERS_IN_CLASSROOM_COUNT,
            Int::class.java,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber)
        )

    fun addOrEditUserInClassroom(orgId: Int, classroomNumber: Int, userId: Int, role: String) {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid

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

    fun getUsersInAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, page: Int, perPage: Int): List<UserAssignment> {
        val assignmentId = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid
        return jdbi.getList(
            GET_USERS_IN_ASSIGNMENT_QUERY,
            UserAssignment::class.java, page, perPage,
            mapOf("assignmentId" to assignmentId)
        )
    }

    fun getUserAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, userId: Int): UserAssignment {
        val assignmentId = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid

        return jdbi.getOne(
            GET_USER_ASSIGNMENT_QUERY,
            UserAssignment::class.java,
            mapOf("userId" to userId, "assignmentId" to assignmentId)
        )
    }

    fun getUsersInAssignmentCount(orgId: Int, classroomNumber: Int, assignmentNumber: Int) =
        jdbi.getOne(
            GET_USERS_IN_ASSIGNMENT_COUNT,
            Int::class.java,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber, "assignmentNumber" to assignmentNumber)
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


    fun tryGetAssignmentOfUser(orgId: Int, classroomNumber: Int, assignmentNumber: Int, userId: Int): Optional<Assignment> {
        val assignment = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber)

        val userMembership = jdbi.tryGetOne(
            GET_USER_ID_IN_ASSIGNMENT,
            String::class.java,
            mapOf(
                "assignmentId" to assignment.aid,
                "userId" to userId,
            )
        )

        if (userMembership.isPresent) return Optional.of(assignment)
        return Optional.empty()
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
}