package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.User
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

private const val GET_USERS_BASE = "SELECT uid, name, gh_id, gh_token FROM USERS"
private const val GET_USER_QUERY = "$GET_USERS_BASE WHERE uid = :userId"

private const val GET_USERS_IN_CLASSROOM_QUERY =
    "$GET_USERS_BASE WHERE uid IN (SELECT uid from USER_CLASSROOM where cid = :classroomId) ORDER BY uid"
private const val GET_USERS_IN_CLASSROOM_COUNT =
    "SELECT COUNT(uid) as count FROM USER_CLASSROOM where cid IN " +
    "(SELECT cid FROM CLASSROOM WHERE org_id = :orgId AND number = :classroomNumber)"

private const val GET_USERS_IN_ASSIGNMENT_QUERY =
    "$GET_USERS_BASE WHERE uid IN (SELECT uid from USER_ASSIGNMENT where aid = :assignmentId) ORDER BY uid"
private const val GET_USERS_IN_ASSIGNMENT_COUNT =
    "SELECT COUNT(uid) as count FROM USER_ASSIGNMENT where aid IN " +
    "(SELECT aid FROM V_ASSIGNMENT WHERE org_id = :orgId AND " +
    "classroom_number = :classroomNumber AND number = :assignmentNumber)"

@Component
class UsersDb(
    val classroomsDb: ClassroomsDb,
    val assignmentsDb: AssignmentsDb,
    val jdbi: Jdbi
) {

    fun getUserById(userId: Int) = jdbi.getOne(GET_USER_QUERY, User::class.java, mapOf("userId" to userId))

    fun getUsersInClassroom(orgId: Int, classroomNumber: Int, page: Int, perPage: Int): List<User> {
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

    fun createUser(name: String, ghId: Int, ghToken: String): User {
        TODO()
    }
}