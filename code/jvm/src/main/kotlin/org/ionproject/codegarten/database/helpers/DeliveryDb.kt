package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dao.DeliveryDao
import org.springframework.stereotype.Component

private const val GET_DELIVERIES_BASE = "SELECT tag, due_date, assignment_id, assignment_name FROM V_DELIVERY"
private const val GET_DELIVERY_QUERY = "$GET_DELIVERIES_BASE WHERE tag = :tag"

private const val GET_DELIVERIES_OF_ASSIGNMENT_QUERY = "$GET_DELIVERIES_BASE WHERE assignment_id = :aid ORDER BY due_date"
private const val GET_DELIVERIES_OF_ASSIGNMENT_COUNT = "SELECT COUNT(tag) as count from DELIVERY WHERE assignment_id = :aid"

@Component
class DeliveryDb : DatabaseHelper() {
    fun getDeliveryByTag(tag: String) = getOne(GET_DELIVERY_QUERY, DeliveryDao::class.java, Pair("tag", tag))

    fun getDeliveriesOfAssignment(assignmentId: Int, page: Int, perPage: Int) =
        getList(GET_DELIVERIES_OF_ASSIGNMENT_QUERY, DeliveryDao::class.java, page, perPage, Pair("aid", assignmentId))
    fun getDeliveriesOfAssignmentCount(assignmentId: Int) = getOne(GET_DELIVERIES_OF_ASSIGNMENT_COUNT, Int::class.java, Pair("aid", assignmentId))
}