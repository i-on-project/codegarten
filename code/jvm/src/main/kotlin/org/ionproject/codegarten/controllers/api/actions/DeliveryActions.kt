package org.ionproject.codegarten.controllers.api.actions

import org.ionproject.codegarten.Routes.INPUT_CONTENT_TYPE
import org.ionproject.codegarten.Routes.getDeliveriesUri
import org.ionproject.codegarten.Routes.getDeliveryByNumberUri
import org.ionproject.codegarten.Routes.getDeliveryOfParticipantUri
import org.ionproject.codegarten.responses.siren.SirenAction
import org.ionproject.codegarten.responses.siren.SirenActionField
import org.ionproject.codegarten.responses.siren.SirenFieldType
import org.springframework.http.HttpMethod

object DeliveryActions {

    fun getCreateDeliveryAction(orgId: Int, classroomNumber: Int, assignmentNumber: Int) = SirenAction(
        name = "create-delivery",
        title = "Create Delivery",
        method = HttpMethod.POST,
        href = getDeliveriesUri(orgId, classroomNumber, assignmentNumber),
        type = INPUT_CONTENT_TYPE,
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
        href = getDeliveryByNumberUri(orgId, classroomNumber, assignmentNumber, deliveryNumber),
        type = INPUT_CONTENT_TYPE,
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
        href = getDeliveryByNumberUri(orgId, classroomNumber, assignmentNumber, deliveryNumber),
        type = INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "assignmentNumber", type = SirenFieldType.hidden, value = assignmentNumber),
            SirenActionField(name = "deliveryNumber", type = SirenFieldType.hidden, value = deliveryNumber),
        )
    )

    fun getSubmitDeliveryAction(orgId: Int, classroomNumber: Int, assignmentNumber: Int, participantId: Int, deliveryNumber: Int) = SirenAction(
        name = "submit-delivery",
        title = "Submit Delivery",
        method = HttpMethod.PUT,
        href = getDeliveryOfParticipantUri(orgId, classroomNumber, assignmentNumber, participantId, deliveryNumber),
        type = INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "assignmentNumber", type = SirenFieldType.hidden, value = assignmentNumber),
            SirenActionField(name = "participantId", type = SirenFieldType.hidden, value = participantId),
            SirenActionField(name = "deliveryNumber", type = SirenFieldType.hidden, value = deliveryNumber),
        )
    )

    fun getDeleteDeliverySubmissionAction(orgId: Int, classroomNumber: Int, assignmentNumber: Int, participantId: Int, deliveryNumber: Int) = SirenAction(
        name = "delete-delivery-submission",
        title = "Delete Delivery Submission",
        method = HttpMethod.DELETE,
        href = getDeliveryOfParticipantUri(orgId, classroomNumber, assignmentNumber, participantId, deliveryNumber),
        type = INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "assignmentNumber", type = SirenFieldType.hidden, value = assignmentNumber),
            SirenActionField(name = "participantId", type = SirenFieldType.hidden, value = participantId),
            SirenActionField(name = "deliveryNumber", type = SirenFieldType.hidden, value = deliveryNumber),
        )
    )
}