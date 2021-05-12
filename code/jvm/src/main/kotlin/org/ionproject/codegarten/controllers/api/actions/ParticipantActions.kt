package org.ionproject.codegarten.controllers.api.actions

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.Routes.USER_PARAM
import org.ionproject.codegarten.Routes.getParticipantsOfAssignmentUri
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.responses.siren.SirenAction
import org.ionproject.codegarten.responses.siren.SirenActionField
import org.ionproject.codegarten.responses.siren.SirenFieldType
import org.springframework.http.HttpMethod
import org.springframework.web.util.UriTemplate

object ParticipantActions {

    fun getAddParticipantToAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, isGroupAssignment: Boolean) = SirenAction(
        name = "add-participant-to-assignment",
        title = "Add Participant To Assignment",
        method = HttpMethod.PUT,
        hrefTemplate = UriTemplate("${getParticipantsOfAssignmentUri(orgId, classroomNumber, assignmentNumber)}/{$USER_PARAM}").includeHost(),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOfNotNull(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "assignmentNumber", type = SirenFieldType.hidden, value = assignmentNumber),
            if (isGroupAssignment) SirenActionField(name = "teamNumber", type = SirenFieldType.number)
            else SirenActionField(name = "userId", type = SirenFieldType.number),
        )
    )

    fun getRemoveParticipantFromAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, isGroupAssignment: Boolean) = SirenAction(
        name = "remove-participant-from-assignment",
        title = "Remove Participant From Assignment",
        method = HttpMethod.DELETE,
        hrefTemplate = UriTemplate("${getParticipantsOfAssignmentUri(orgId, classroomNumber, assignmentNumber)}/{$USER_PARAM}").includeHost(),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "assignmentNumber", type = SirenFieldType.hidden, value = assignmentNumber),
            if (isGroupAssignment) SirenActionField(name = "teamNumber", type = SirenFieldType.number)
            else SirenActionField(name = "userId", type = SirenFieldType.number),
        )
    )
}