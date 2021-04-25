package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.Assignment
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

private const val GET_ASSIGNMENTS_BASE =
    "SELECT aid, number, name, description, type, repo_prefix, template, org_id, classroom_id, classroom_number, classroom_name FROM V_ASSIGNMENT"
private const val GET_ASSIGNMENT_QUERY =
    "$GET_ASSIGNMENTS_BASE WHERE org_id = :orgId AND classroom_number = :classroomNumber AND number = :number"

private const val GET_ASSIGNMENTS_OF_USER_QUERY =
    "$GET_ASSIGNMENTS_BASE WHERE org_id = :orgId AND classroom_number = :classroomNumber AND " +
    "aid IN (SELECT aid from USER_ASSIGNMENT where uid = :userId) ORDER BY number"
private const val GET_ASSIGNMENTS_OF_USER_COUNT =
    "SELECT COUNT(aid) as count FROM V_ASSIGNMENT WHERE org_id = :orgId AND classroom_number = :classroomNumber AND " +
    "aid IN (SELECT aid from USER_ASSIGNMENT where uid = :userId)"

@Component
class AssignmentsDb(
    val classroomsDb: ClassroomsDb,
    val jdbi: Jdbi
) {

    fun getAssignmentByNumber(orgId: Int, classroomNumber: Int, assignmentNumber: Int) =
        jdbi.getOne(
            GET_ASSIGNMENT_QUERY,
            Assignment::class.java,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber, "number" to assignmentNumber)
        )

    fun getAssignmentsOfUser(orgId: Int, classroomNumber: Int, userId: Int, page: Int, perPage: Int): List<Assignment> {
        classroomsDb.getClassroomByNumber(orgId, classroomNumber) // Check if classroom exists (will throw exception if not found)
        return jdbi.getList(
            GET_ASSIGNMENTS_OF_USER_QUERY,
            Assignment::class.java, page, perPage,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber, "userId" to userId)
        )
    }

    fun getAssignmentsOfUserCount(orgId: Int, classroomNumber: Int, userId: Int) =
        jdbi.getOne(
            GET_ASSIGNMENTS_OF_USER_COUNT,
            Int::class.java,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber, "userId" to userId)
        )
}
