package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.Delivery
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

private const val GET_DELIVERIES_BASE =
    "SELECT did, number, tag, due_date, assignment_id, assignment_number, assignment_name, " +
    "org_id, classroom_id, classroom_number, classroom_name FROM V_DELIVERY"
private const val GET_DELIVERY_QUERY =
    "$GET_DELIVERIES_BASE WHERE org_id = :orgId AND classroom_number = :classroomNumber AND " +
    "assignment_number = :assignmentNumber AND number = :number"
private const val GET_DELIVERY_BY_ID_QUERY = "$GET_DELIVERIES_BASE WHERE did = :deliveryId"

private const val GET_DELIVERIES_OF_ASSIGNMENT_QUERY =
    "$GET_DELIVERIES_BASE WHERE org_id = :orgId AND classroom_number = :classroomNumber AND " +
    "assignment_number = :assignmentNumber ORDER BY number"
private const val GET_DELIVERIES_OF_ASSIGNMENT_COUNT =
    "SELECT COUNT(did) as count from V_DELIVERY WHERE org_id = :orgId AND " +
    "classroom_number = :classroomNumber AND assignment_number = :assignmentNumber"

private const val CREATE_DELIVERY_QUERY =
    "INSERT INTO DELIVERY(aid, tag, due_date) VALUES(:assignmentId, :tag, :dueDate)"

private const val UPDATE_DELIVERY_START = "UPDATE DELIVERY SET"
private const val UPDATE_DELIVERY_END = "WHERE did = :deliveryId"

private const val DELETE_DELIVERY_QUERY = "DELETE FROM DELIVERY WHERE did = :deliveryId"

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

    fun getDeliveriesOfAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, page: Int, limit: Int) =
        jdbi.getList(
            GET_DELIVERIES_OF_ASSIGNMENT_QUERY,
            Delivery::class.java, page, limit,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber, "assignmentNumber" to assignmentNumber)
        )

    fun getDeliveriesOfAssignmentCount(orgId: Int, classroomNumber: Int, assignmentNumber: Int) =
        jdbi.getOne(
            GET_DELIVERIES_OF_ASSIGNMENT_COUNT,
            Int::class.java,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber, "assignmentNumber" to assignmentNumber)
        )

    fun createDelivery(orgId: Int, classroomNumber: Int, assignmentNumber: Int,
                       tag: String, dueDate: OffsetDateTime? = null): Delivery {
        val assignmentId = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid

        return jdbi.insertAndGet(
            CREATE_DELIVERY_QUERY, Int::class.java,
            GET_DELIVERY_BY_ID_QUERY, Delivery::class.java,
            mapOf(
                "assignmentId" to assignmentId,
                "tag" to tag,
                "dueDate" to dueDate
            ),
            "deliveryId"
        )
    }

    fun editDelivery(orgId: Int, classroomNumber: Int, assignmentNumber: Int, deliveryNumber: Int,
                     tag: String? = null, dueDate: OffsetDateTime? = null) {
        if (tag == null && dueDate == null) {
            return
        }

        val deliveryId = getDeliveryByNumber(orgId, classroomNumber, assignmentNumber, deliveryNumber).did

        val updateFields = mutableMapOf<String, Any>()
        if (tag != null) updateFields["tag"] = tag
        if (dueDate != null) updateFields["due_date"] = dueDate

        return jdbi.update(
            UPDATE_DELIVERY_START,
            updateFields,
            UPDATE_DELIVERY_END,
            mapOf("deliveryId" to deliveryId)
        )
    }

    fun deleteDelivery(orgId: Int, classroomNumber: Int, assignmentNumber: Int, deliveryNumber: Int) {
        val deliveryId = getDeliveryByNumber(orgId, classroomNumber, assignmentNumber, deliveryNumber).did
        jdbi.delete(
            DELETE_DELIVERY_QUERY,
            mapOf(
                "deliveryId" to deliveryId
            )
        )
    }
}