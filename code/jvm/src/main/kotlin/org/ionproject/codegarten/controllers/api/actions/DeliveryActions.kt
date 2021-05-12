package org.ionproject.codegarten.controllers.api.actions

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.responses.siren.SirenAction
import org.ionproject.codegarten.responses.siren.SirenActionField
import org.ionproject.codegarten.responses.siren.SirenFieldType
import org.springframework.http.HttpMethod

object DeliveryActions {

    fun getCreateDeliveryAction(orgId: Int, classroomNumber: Int, assignmentNumber: Int) = SirenAction(
        name = "create-delivery",
        title = "Create Delivery",
        method = HttpMethod.POST,
        href = Routes.getDeliveriesUri(orgId, classroomNumber, assignmentNumber).includeHost(),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "assignmentNumber", type = SirenFieldType.hidden, value = assignmentNumber),
            SirenActionField(name = "tag", type = SirenFieldType.text),
            SirenActionField(name = "dueDate", type = SirenFieldType.datetime)
        )
    )

    fun getEditDeliveryAction(orgId: Int, classroomNumber: Int, assignmentNumber: Int, deliveryNumber: Int) = SirenAction(
        name = "edit-delivery",
        title = "Edit Delivery",
        method = HttpMethod.PUT,
        href = Routes.getDeliveryByNumberUri(orgId, classroomNumber, assignmentNumber, deliveryNumber).includeHost(),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "assignmentNumber", type = SirenFieldType.hidden, value = assignmentNumber),
            SirenActionField(name = "deliveryNumber", type = SirenFieldType.hidden, value = deliveryNumber),
            SirenActionField(name = "tag", type = SirenFieldType.text),
            SirenActionField(name = "dueDate", type = SirenFieldType.datetime)
        )
    )

    fun getDeleteDeliveryAction(orgId: Int, classroomNumber: Int, assignmentNumber: Int, deliveryNumber: Int) = SirenAction(
        name = "delete-delivery",
        title = "Delete Delivery",
        method = HttpMethod.DELETE,
        href = Routes.getDeliveryByNumberUri(orgId, classroomNumber, assignmentNumber, deliveryNumber).includeHost(),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "assignmentNumber", type = SirenFieldType.hidden, value = assignmentNumber),
            SirenActionField(name = "deliveryNumber", type = SirenFieldType.hidden, value = deliveryNumber),
        )
    )
}