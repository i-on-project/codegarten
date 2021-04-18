package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dao.UserDao
import org.springframework.stereotype.Component

private const val GET_USERS_BASE = "SELECT uid, name, gh_id, gh_token FROM USERS"
private const val GET_USER_QUERY = "$GET_USERS_BASE WHERE uid = :uid"

private const val GET_USERS_IN_CLASSROOM_QUERY = "$GET_USERS_BASE WHERE uid IN (SELECT uid from USER_CLASSROOM where cid = :cid) ORDER BY uid"
private const val GET_USERS_IN_CLASSROOM_COUNT = "SELECT COUNT(uid) as count from USER_CLASSROOM where cid = :cid"

private const val GET_USERS_IN_ASSIGNMENT_QUERY = "$GET_USERS_BASE WHERE uid IN (SELECT uid from USER_ASSIGNMENT where aid = :aid) ORDER BY uid"
private const val GET_USERS_IN_ASSIGNMENT_COUNT = "SELECT COUNT(uid) as count from USER_ASSIGNMENT where aid = :aid"

@Component
class UsersDb : DatabaseHelper() {

    fun getUserById(userId: Int) = getOne(GET_USER_QUERY, UserDao::class.java, Pair("uid", userId))

    fun getUsersInClassroom(classroomId: Int, page: Int, perPage: Int) =
        getList(GET_USERS_IN_CLASSROOM_QUERY, UserDao::class.java, page, perPage, Pair("cid", classroomId))
    fun getUsersInClassroomCount(classroomId: Int) = getOne(GET_USERS_IN_CLASSROOM_COUNT, Int::class.java, Pair("cid", classroomId))

    fun getUsersInAssignment(assignmentId: Int, page: Int, perPage: Int) =
        getList(GET_USERS_IN_ASSIGNMENT_QUERY, UserDao::class.java, page, perPage, Pair("aid", assignmentId))
    fun getUsersInAssignmentCount(assignmentId: Int) = getOne(GET_USERS_IN_ASSIGNMENT_COUNT, Int::class.java, Pair("aid", assignmentId))
}