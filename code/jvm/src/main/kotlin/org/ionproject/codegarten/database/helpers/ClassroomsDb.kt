package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.Classroom
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

private const val GET_CLASSROOMS_BASE = "SELECT cid, number, inv_code, org_id, name, description FROM V_CLASSROOM"
private const val GET_CLASSROOM_QUERY = "$GET_CLASSROOMS_BASE WHERE org_id = :orgId AND number = :classroomNumber ORDER BY number"

private const val GET_CLASSROOM_BY_ID_QUERY = "$GET_CLASSROOMS_BASE WHERE cid = :classroomId"

private const val GET_CLASSROOMS_OF_USER_QUERY =
    "$GET_CLASSROOMS_BASE WHERE org_id = :orgId AND cid IN (SELECT cid from USER_CLASSROOM where uid = :userId) ORDER BY number"
private const val GET_CLASSROOMS_OF_USER_COUNT =
    "SELECT COUNT(cid) as count FROM CLASSROOM WHERE org_id = :orgId AND cid IN (SELECT cid from USER_CLASSROOM where uid = :userId)"

private const val CREATE_CLASSROOM_QUERY = "INSERT INTO CLASSROOM(org_id, name, description) VALUES(:orgId, :name, :description)"

private const val UPDATE_CLASSROOM_START = "UPDATE CLASSROOM SET"
private const val UPDATE_CLASSROOM_END = "WHERE number = :classroomNumber AND org_id = :orgId"

private const val DELETE_CLASSROOM_QUERY = "DELETE FROM CLASSROOM WHERE org_id = :orgId and number = :classroomNumber"

@Component
class ClassroomsDb(val jdbi: Jdbi) {

    fun getClassroomByNumber(orgId: Int, classroomNumber: Int) =
        jdbi.getOne(
            GET_CLASSROOM_QUERY,
            Classroom::class.java,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber)
        )

    fun getClassroomById(classroomId: Int) =
        jdbi.getOne(
            GET_CLASSROOM_BY_ID_QUERY,
            Classroom::class.java,
            mapOf("classroomId" to classroomId)
        )

    fun getClassroomsOfUser(orgId: Int, userId: Int, page: Int, limit: Int) =
        jdbi.getList(
            GET_CLASSROOMS_OF_USER_QUERY,
            Classroom::class.java,
            page, limit,
            mapOf(
                "userId" to userId,
                "orgId" to orgId
            )
        )

    fun getClassroomsOfUserCount(orgId: Int, userId: Int) =
        jdbi.getOne(
            GET_CLASSROOMS_OF_USER_COUNT,
            Int::class.java,
            mapOf(
                "orgId" to orgId,
                "userId" to userId
            )
        )

    fun createClassroom(orgId: Int, name: String, description: String?) =
        jdbi.insertAndGet(
            CREATE_CLASSROOM_QUERY, Classroom::class.java,
            mapOf(
                "orgId" to orgId,
                "name" to name,
                "description" to description
            )
        )

    fun editClassroom(orgId: Int, classroomNumber: Int, name: String?, description: String?) {
        if (name == null && description == null) {
            return
        }

        val updateFields = mutableMapOf<String, Any>()
        if (name != null) updateFields["name"] = name
        if (description != null) updateFields["description"] = description

        jdbi.update(
            UPDATE_CLASSROOM_START,
            updateFields,
            UPDATE_CLASSROOM_END,
            mapOf(
                "classroomNumber" to classroomNumber,
                "orgId" to orgId
            )
        )
    }

    fun deleteClassroom(orgId: Int, classroomNumber: Int) {
        jdbi.delete(
            DELETE_CLASSROOM_QUERY,
            mapOf(
                "orgId" to orgId,
                "classroomNumber" to classroomNumber
            )
        )
    }
}