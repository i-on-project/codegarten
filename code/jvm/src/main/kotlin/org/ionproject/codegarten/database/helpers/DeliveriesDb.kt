package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.CreatedDelivery
import org.ionproject.codegarten.database.dto.Delivery
import org.ionproject.codegarten.database.dto.DtoListWrapper
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

private const val GET_DELIVERY_BASE =
    "SELECT did, number, tag, due_date, assignment_id, assignment_number, assignment_name, " +
    "org_id, classroom_id, classroom_number, classroom_name FROM V_DELIVERY"
private const val GET_DELIVERIES_BASE =
    "SELECT did, number, tag, due_date, assignment_id, assignment_number, assignment_name, " +
            "org_id, classroom_id, classroom_number, classroom_name, COUNT(*) OVER() as count FROM V_DELIVERY"

private const val GET_DELIVERY_QUERY =
    "$GET_DELIVERY_BASE WHERE org_id = :orgId AND classroom_number = :classroomNumber AND " +
    "assignment_number = :assignmentNumber AND number = :number"
private const val GET_DELIVERY_BY_ID_QUERY = "$GET_DELIVERY_BASE WHERE did = :deliveryId"

private const val GET_DELIVERIES_OF_ASSIGNMENT_QUERY =
    "$GET_DELIVERIES_BASE WHERE org_id = :orgId AND classroom_number = :classroomNumber AND " +
    "assignment_number = :assignmentNumber ORDER BY due_date, number"

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

    fun getDeliveriesOfAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, page: Int, limit: Int): DtoListWrapper<Delivery> {
        val results = jdbi.getList(
            GET_DELIVERIES_OF_ASSIGNMENT_QUERY,
            Delivery::class.java, page, limit,
            mapOf("orgId" to orgId, "classroomNumber" to classroomNumber, "assignmentNumber" to assignmentNumber)
        )

        return DtoListWrapper(
            count = if (results.isEmpty()) 0 else results[0].count!!,
            results = results
        )
    }

    fun createDelivery(orgId: Int, classroomNumber: Int, assignmentNumber: Int,
                       tag: String, dueDate: OffsetDateTime? = null): Delivery {
        val assignment = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber)

        val createdDelivery = jdbi.insertAndGet(
            CREATE_DELIVERY_QUERY, CreatedDelivery::class.java,
            mapOf(
                "assignmentId" to assignment.aid,
                "tag" to tag,
                "dueDate" to dueDate
            )
        )

        return Delivery(
            did = createdDelivery.did,
            number = createdDelivery.number,
            tag = createdDelivery.tag,
            due_date = createdDelivery.due_date,

            assignment_id = assignment.aid,
            assignment_number = assignment.number,
            assignment_name = assignment.name,

            org_id = assignment.org_id,
            classroom_id = assignment.classroom_id,
            classroom_number = assignment.classroom_number,
            classroom_name = assignment.classroom_name,
        )
    }

    fun editDelivery(orgId: Int, classroomNumber: Int, assignmentNumber: Int, deliveryNumber: Int,
                     tag: String? = null, dueDate: OffsetDateTime? = null, deleteDate: Boolean = false) {
        if (tag == null && dueDate == null && !deleteDate) {
            return
        }

        val deliveryId = getDeliveryByNumber(orgId, classroomNumber, assignmentNumber, deliveryNumber).did

        val updateFields = mutableMapOf<String, Any?>()
        if (tag != null) updateFields["tag"] = tag

        if (deleteDate) updateFields["due_date"] = null
        else if (dueDate != null) updateFields["due_date"] = dueDate

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