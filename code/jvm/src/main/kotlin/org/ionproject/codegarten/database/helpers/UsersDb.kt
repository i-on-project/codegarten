package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.Assignment
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserClassroom
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
    "$GET_USERS_BASE WHERE uid IN (SELECT uid from USER_CLASSROOM where cid = :classroomId) ORDER BY uid"
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

private const val GET_USER_MEMBERSHIP_IN_CLASSROOM_QUERY =
    "SELECT type from USER_CLASSROOM where uid = :userId AND cid = :classroomId"

private const val GET_USERS_IN_ASSIGNMENT_QUERY =
    "$GET_USERS_BASE WHERE uid IN (SELECT uid from USER_ASSIGNMENT where aid = :assignmentId) ORDER BY uid"
private const val GET_USERS_IN_ASSIGNMENT_COUNT =
    "SELECT COUNT(uid) as count FROM USER_ASSIGNMENT where aid IN " +
        "(SELECT aid FROM V_ASSIGNMENT WHERE org_id = :orgId AND " +
        "classroom_number = :classroomNumber AND number = :assignmentNumber)"

private const val GET_USER_IN_ASSIGNMENT = "SELECT uid from USER_ASSIGNMENT where aid = :assignmentId"

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

    fun getUsersInClassroom(orgId: Int, classroomNumber: Int, page: Int, perPage: Int): List<User> {
        //TODO: Use a view to get membership info

        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        return jdbi.getList(
            GET_USERS_IN_CLASSROOM_QUERY,
            User::class.java, page, perPage,
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

        val userMembership = jdbi.tryGetOne(
            GET_USER_MEMBERSHIP_IN_CLASSROOM_QUERY,
            String::class.java,
            mapOf(
                "userId" to userId,
                "classroomId" to classroom.cid
            )
        )

        return UserClassroom(
            role = if (userMembership.isEmpty) NOT_A_MEMBER else UserClassroomMembership.valueOf(userMembership.get().toUpperCase()),
            classroom = classroom
        )
    }

    fun getUsersInAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, page: Int, perPage: Int): List<User> {
        val assignmentId = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid
        return jdbi.getList(
            GET_USERS_IN_ASSIGNMENT_QUERY,
            User::class.java, page, perPage,
            mapOf("assignmentId" to assignmentId)
        )
    }
    fun getUsersInAssignmentCount(orgId: Int, classroomNumber: Int, assignmentNumber: Int) =
        jdbi.getOne(
            GET_USERS_IN_ASSIGNMENT_COUNT,
            Int::class.java,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber, "assignmentNumber" to assignmentNumber)
        )


    fun tryGetAssignmentOfUser(orgId: Int, classroomNumber: Int, assignmentNumber: Int, userId: Int): Optional<Assignment> {
        val assignment = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber)

        val userMembership = jdbi.tryGetOne(
            GET_USER_IN_ASSIGNMENT,
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