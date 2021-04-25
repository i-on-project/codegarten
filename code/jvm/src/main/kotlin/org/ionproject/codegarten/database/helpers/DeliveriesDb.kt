package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.Delivery
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

private const val GET_DELIVERIES_BASE =
    "SELECT did, number, tag, due_date, assignment_id, assignment_number, assignment_name, " +
    "org_id, classroom_id, classroom_number, classroom_name FROM V_DELIVERY"
private const val GET_DELIVERY_QUERY =
    "$GET_DELIVERIES_BASE WHERE org_id = :orgId AND classroom_number = :classroomNumber AND " +
    "assignment_number = :assignmentNumber AND number = :number"

private const val GET_DELIVERIES_OF_ASSIGNMENT_QUERY =
    "$GET_DELIVERIES_BASE WHERE org_id = :orgId AND classroom_number = :classroomNumber AND" +
    "assignment_number = :assignmentNumber ORDER BY number"
private const val GET_DELIVERIES_OF_ASSIGNMENT_COUNT =
    "SELECT COUNT(did) as count from DELIVERY WHERE org_id = :orgId AND " +
    "classroom_number = :classroomNumber AND assignment_number = :assignmentNumber"

@Component
class DeliveriesDb(
    val assignmentsDb: AssignmentsDb,
    val jdbi: Jdbi
) {

    fun getDeliveryByNumber(orgId: Int, classroomNumber: Int, assignmentNumber: Int, deliveryNumber: Int) =
        jdbi.getOne(
            GET_DELIVERY_QUERY,
            Delivery::class.java,
            mapOf(
                "orgId" to orgId,
                "classroomNumber" to classroomNumber,
                "assignmentNumber" to assignmentNumber,
                "number" to deliveryNumber
            )
        )

    fun getDeliveriesOfAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, page: Int, perPage: Int): List<Delivery> {
        // Check if assignment exists (will throw exception if not found)
        assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber)
        return jdbi.getList(
            GET_DELIVERIES_OF_ASSIGNMENT_QUERY,
            Delivery::class.java, page, perPage,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber, "assignmentNumber" to assignmentNumber)
        )
    }

    fun getDeliveriesOfAssignmentCount(orgId: Int, classroomNumber: Int, assignmentNumber: Int) =
        jdbi.getOne(
            GET_DELIVERIES_OF_ASSIGNMENT_COUNT,
            Int::class.java,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber, "assignmentNumber" to assignmentNumber)
        )
}