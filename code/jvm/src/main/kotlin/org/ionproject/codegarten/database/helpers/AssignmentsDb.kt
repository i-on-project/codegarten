package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.Assignment
import org.ionproject.codegarten.database.dto.CreatedAssignment
import org.ionproject.codegarten.database.dto.DtoListWrapper
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

private const val GET_ASSIGNMENT_BASE =
    "SELECT aid, number, inv_code, name, description, type, repo_prefix, repo_template, org_id, classroom_id, classroom_number, classroom_name FROM V_ASSIGNMENT"
private const val GET_ASSIGNMENTS_BASE =
    "SELECT aid, number, inv_code, name, description, type, repo_prefix, repo_template, org_id, classroom_id, classroom_number, classroom_name, COUNT(*) OVER() as count FROM V_ASSIGNMENT"

private const val GET_ASSIGNMENT_QUERY =
    "$GET_ASSIGNMENT_BASE WHERE org_id = :orgId AND classroom_number = :classroomNumber AND number = :number"
private const val GET_ASSIGNMENT_BY_ID_QUERY =
    "$GET_ASSIGNMENT_BASE WHERE aid = :assignmentId"
private const val GET_ASSIGNMENT_BY_INVCODE_QUERY =
    "$GET_ASSIGNMENT_BASE WHERE inv_code = :inviteCode"

private const val GET_ASSIGNMENTS_OF_CLASSROOM_QUERY =
    "$GET_ASSIGNMENTS_BASE WHERE org_id = :orgId AND classroom_number = :classroomNumber ORDER BY number"

private const val GET_ASSIGNMENTS_IDS_OF_USER_QUERY =
    "SELECT aid FROM USER_ASSIGNMENT WHERE uid = :userId UNION SELECT aid FROM V_TEAM_USER_ASSIGNMENT WHERE uid = :userId"
private const val GET_ASSIGNMENTS_OF_USER_QUERY =
    "$GET_ASSIGNMENTS_BASE WHERE org_id = :orgId AND classroom_number = :classroomNumber AND " +
    "aid IN ($GET_ASSIGNMENTS_IDS_OF_USER_QUERY) ORDER BY number"

private const val CREATE_ASSIGNMENT_QUERY =
    "INSERT INTO ASSIGNMENT(cid, name, description, type, repo_prefix, repo_template) VALUES" +
    "(:classroomId, :name, :description, :type, :repoPrefix, :repoTemplateId)"

private const val UPDATE_ASSIGNMENT_START = "UPDATE ASSIGNMENT SET"
private const val UPDATE_ASSIGNMENT_END = "WHERE aid = :assignmentId"

private const val DELETE_ASSIGNMENT_QUERY = "DELETE FROM ASSIGNMENT WHERE aid = :assignmentId"

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

    fun getAssignmentById(assignmentId: Int) =
        jdbi.getOne(
            GET_ASSIGNMENT_BY_ID_QUERY,
            Assignment::class.java,
            mapOf("assignmentId" to assignmentId)
        )

    fun tryGetAssignmentByInviteCode(inviteCode: String) =
        jdbi.tryGetOne(
            GET_ASSIGNMENT_BY_INVCODE_QUERY,
            Assignment::class.java,
            mapOf("inviteCode" to inviteCode)
        )

    fun getAllAssignments(orgId: Int, classroomNumber: Int, page: Int, limit: Int): DtoListWrapper<Assignment> {
        val results = jdbi.getList(
            GET_ASSIGNMENTS_OF_CLASSROOM_QUERY,
            Assignment::class.java, page, limit,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber)
        )

        return DtoListWrapper(
            count = if (results.isEmpty()) 0 else results[0].count!!,
            results = results
        )
    }

    fun getAssignmentsOfUser(orgId: Int, classroomNumber: Int, userId: Int, page: Int, limit: Int): DtoListWrapper<Assignment> {
        val results = jdbi.getList(
            GET_ASSIGNMENTS_OF_USER_QUERY,
            Assignment::class.java, page, limit,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber, "userId" to userId)
        )

        return DtoListWrapper(
            count = if (results.isEmpty()) 0 else results[0].count!!,
            results = results
        )
    }

    fun createAssignment(
        orgId: Int, classroomNumber: Int, name: String, description: String? = null, type: String,
        repoPrefix: String, repoTemplateId: Int? = null
    ): Assignment {

        val classroom = classroomsDb.getClassroomByNumber(orgId, classroomNumber)

        val createdAssignment = jdbi.insertAndGet(
            CREATE_ASSIGNMENT_QUERY, CreatedAssignment::class.java,
            mapOf(
                "classroomId" to classroom.cid,
                "name" to name,
                "description" to description,
                "type" to type,
                "repoPrefix" to repoPrefix,
                "repoTemplateId" to repoTemplateId
            )
        )

        return Assignment(
            aid = createdAssignment.aid,
            number = createdAssignment.number,
            inv_code = null,
            name = createdAssignment.name,
            description = createdAssignment.description,
            type = createdAssignment.type,
            repo_prefix = createdAssignment.repo_prefix,
            repo_template = createdAssignment.repo_template,

            org_id = classroom.org_id,
            classroom_id = classroom.cid,
            classroom_number = classroom.number,
            classroom_name = classroom.name
        )
    }

    fun editAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int,
                       name: String? = null, description: String? = null) {
        if (name == null && description == null) {
            return
        }

        val assignmentId = getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid

        val updateFields = mutableMapOf<String, Any>()
        if (name != null) updateFields["name"] = name
        if (description != null) updateFields["description"] = description

        jdbi.update(
            UPDATE_ASSIGNMENT_START,
            updateFields,
            UPDATE_ASSIGNMENT_END,
            mapOf("assignmentId" to assignmentId)
        )
    }

    fun deleteAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int) {
        val assignmentId = getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid
        jdbi.delete(
            DELETE_ASSIGNMENT_QUERY,
            mapOf("assignmentId" to assignmentId)
        )
    }
}
