package org.ionproject.codegarten.controllers.api.actions

import org.ionproject.codegarten.Routes.INPUT_CONTENT_TYPE
import org.ionproject.codegarten.Routes.getTeamByNumberUri
import org.ionproject.codegarten.Routes.getTeamsUri
import org.ionproject.codegarten.responses.siren.SirenAction
import org.ionproject.codegarten.responses.siren.SirenActionField
import org.ionproject.codegarten.responses.siren.SirenFieldType
import org.springframework.http.HttpMethod

object TeamActions {

    fun getCreateTeamAction(orgId: Int, classroomNumber: Int) = SirenAction(
        name = "create-team",
        title = "Create Team",
        method = HttpMethod.POST,
        href = getTeamsUri(orgId, classroomNumber),
        type = INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "name", type = SirenFieldType.text)
        )
    )

    fun getEditTeamAction(orgId: Int, classroomNumber: Int, teamNumber: Int) = SirenAction(
        name = "edit-team",
        title = "Edit Team",
        method = HttpMethod.PUT,
        href = getTeamByNumberUri(orgId, classroomNumber, teamNumber),
        type = INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "teamNumber", type = SirenFieldType.hidden, value = teamNumber),
            SirenActionField(name = "name", type = SirenFieldType.text)
        )
    )

    fun getDeleteTeamAction(orgId: Int, classroomNumber: Int, teamNumber: Int) = SirenAction(
        name = "delete-team",
        title = "Delete Team",
        method = HttpMethod.DELETE,
        href = getTeamByNumberUri(orgId, classroomNumber, teamNumber),
        type = INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "teamNumber", type = SirenFieldType.hidden, value = teamNumber)
        )
    )
}