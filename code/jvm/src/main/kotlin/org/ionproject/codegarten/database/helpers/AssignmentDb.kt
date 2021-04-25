package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dao.AssignmentDao
import org.springframework.stereotype.Component

private const val GET_ASSIGNMENTS_BASE = "SELECT aid, name, description, type, repo_prefix, template, classroom_id, classroom_name FROM V_ASSIGNMENT"
private const val GET_ASSIGNMENT_QUERY = "$GET_ASSIGNMENTS_BASE WHERE aid = :aid"

private const val GET_ASSIGNMENTS_OF_USER_QUERY = "$GET_ASSIGNMENTS_BASE WHERE aid IN (SELECT aid from USER_ASSIGNMENT where uid = :uid) ORDER BY aid"
private const val GET_ASSIGNMENTS_OF_USER_COUNT = "SELECT COUNT(cid) as count from USER_ASSIGNMENT WHERE uid = :uid"

private const val GET_ASSIGNMENTS_OF_USER_IN_CLASSROOM_QUERY = "$GET_ASSIGNMENTS_BASE WHERE classroom_id = :cid AND aid IN (SELECT aid from USER_ASSIGNMENT where uid = :uid) ORDER BY aid"
private const val GET_ASSIGNMENTS_OF_USER_IN_CLASSROOM_COUNT = "SELECT COUNT(cid) as count from USER_ASSIGNMENT WHERE uid = :uid AND cid = :cid"

private const val GET_ASSIGNMENTS_OF_CLASSROOM_QUERY = "$GET_ASSIGNMENTS_BASE WHERE classroom_id = :cid"
private const val GET_ASSIGNMENTS_OF_CLASSROOM_COUNT = "SELECT COUNT(aid) as count from ASSIGNMENT WHERE cid = :cid"

@Component
class AssignmentDb : DatabaseHelper() {
    fun getAssignmentById(assignmentId: Int) = getOne(GET_ASSIGNMENT_QUERY, AssignmentDao::class.java, Pair("cid", assignmentId))

    fun getAssignmentsOfUser(userId: Int, page: Int, perPage: Int) =
        getList(GET_ASSIGNMENTS_OF_USER_QUERY, AssignmentDao::class.java, page, perPage, Pair("uid", userId))
    fun getAssignmentsOfUserCount(userId: Int) = getOne(GET_ASSIGNMENTS_OF_USER_COUNT, Int::class.java, Pair("uid", userId))

    fun getAssignmentsOfUserInClassroom(userId: Int, classroomId: Int, page: Int, perPage: Int) =
        getList(GET_ASSIGNMENTS_OF_USER_IN_CLASSROOM_QUERY, AssignmentDao::class.java, page, perPage,
            Pair("uid", userId),
            Pair("cid", classroomId))
    fun getAssignmentsOfUserInClassroomCount(userId: Int, classroomId: Int) =
        getOne(GET_ASSIGNMENTS_OF_USER_IN_CLASSROOM_COUNT, Int::class.java,
            Pair("uid", userId),
            Pair("cid", classroomId))

    fun getAssignmentsOfClassroom(classroomId: Int, page: Int, perPage: Int) =
        getList(GET_ASSIGNMENTS_OF_CLASSROOM_QUERY, AssignmentDao::class.java, page, perPage, Pair("cid", classroomId))
    fun getAssignmentsOfClassroomCount(classroomId: Int) = getOne(GET_ASSIGNMENTS_OF_CLASSROOM_COUNT, Int::class.java, Pair("cid", classroomId))
}
