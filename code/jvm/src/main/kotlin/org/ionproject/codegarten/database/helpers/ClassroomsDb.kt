package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.Classroom
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

private const val GET_CLASSROOMS_BASE = "SELECT cid, number, org_id, name, description FROM CLASSROOM"
private const val GET_CLASSROOM_QUERY = "$GET_CLASSROOMS_BASE WHERE org_id = :orgId AND number = :classroomNumber ORDER BY number"

private const val GET_CLASSROOMS_OF_USER_QUERY =
    "$GET_CLASSROOMS_BASE WHERE org_id = :orgId AND cid IN (SELECT cid from USER_CLASSROOM where uid = :userId) ORDER BY number"
private const val GET_CLASSROOMS_OF_USER_COUNT =
    "SELECT COUNT(cid) as count FROM CLASSROOM WHERE org_id = :orgId AND cid IN (SELECT cid from USER_CLASSROOM where uid = :userId)"

@Component
class ClassroomsDb(val jdbi: Jdbi) {

    fun getClassroomByNumber(orgId: Int, classroomNumber: Int) =
        jdbi.getOne(
            GET_CLASSROOM_QUERY,
            Classroom::class.java,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber)
        )

    fun getClassroomsOfUser(orgId: Int, userId: Int, page: Int, perPage: Int) =
        jdbi.getList(
            GET_CLASSROOMS_OF_USER_QUERY,
            Classroom::class.java,
            page, perPage,
            mapOf("userId" to userId)
        )

    fun getClassroomsOfUserCount(orgId: Int, userId: Int) =
        jdbi.getOne(
            GET_CLASSROOMS_OF_USER_COUNT,
            Int::class.java,
            mapOf("orgId" to orgId, "userId" to userId)
        )
}