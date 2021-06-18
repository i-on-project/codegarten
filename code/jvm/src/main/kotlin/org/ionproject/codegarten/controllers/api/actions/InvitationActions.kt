package org.ionproject.codegarten.controllers.api.actions

import org.ionproject.codegarten.Routes.INPUT_CONTENT_TYPE
import org.ionproject.codegarten.Routes.getUserInviteUri
import org.ionproject.codegarten.responses.siren.SirenAction
import org.ionproject.codegarten.responses.siren.SirenActionField
import org.ionproject.codegarten.responses.siren.SirenFieldType
import org.springframework.http.HttpMethod

object InvitationActions {

    fun getJoinClassroomInviteAction(invCode: String) = SirenAction(
        name = "join-classroom",
        title = "Join Classroom",
        method = HttpMethod.PUT,
        href = getUserInviteUri(invCode),
        type = INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "inviteCode", type = SirenFieldType.hidden, value = invCode),
            SirenActionField(name = "teamId", type = SirenFieldType.number),
        )
    )

    fun getJoinAssignmentInviteAction(invCode: String) = SirenAction(
        name = "join-assignment",
        title = "Join Assignment",
        method = HttpMethod.PUT,
        href = getUserInviteUri(invCode),
        type = INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "inviteCode", type = SirenFieldType.hidden, value = invCode),
            SirenActionField(name = "teamId", type = SirenFieldType.number),
        )
    )
}