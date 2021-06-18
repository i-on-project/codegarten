package org.ionproject.codegarten.controllers.api.actions

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.Routes.USER_HREF
import org.ionproject.codegarten.Routes.USER_PARAM
import org.ionproject.codegarten.Routes.getUsersOfClassroomUri
import org.ionproject.codegarten.Routes.getUsersOfTeamUri
import org.ionproject.codegarten.responses.siren.SirenAction
import org.ionproject.codegarten.responses.siren.SirenActionField
import org.ionproject.codegarten.responses.siren.SirenFieldType
import org.ionproject.codegarten.responses.siren.SirenOneOf
import org.springframework.http.HttpMethod
import org.springframework.web.util.UriTemplate
import java.net.URI

object UserActions {

    fun getEditUserAction() = SirenAction(
        name = "edit-user",
        title = "Edit User",
        method = HttpMethod.PUT,
        href = URI(USER_HREF),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "name", type = SirenFieldType.text),
        )
    )

    fun getDeleteUserAction() = SirenAction(
        name = "delete-user",
        title = "Delete User",
        method = HttpMethod.DELETE,
        href = URI(USER_HREF)
    )

    // Classrooms

    fun getAddUserToClassroom(orgId: Int, classroomNumber: Int) = SirenAction(
        name = "add-user-to-classroom",
        title = "Add User To Classroom",
        method = HttpMethod.PUT,
        hrefTemplate = UriTemplate("${getUsersOfClassroomUri(orgId, classroomNumber)}/{$USER_PARAM}"),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "userId", type = SirenFieldType.number),
            SirenActionField(name = "role", type = SirenFieldType.text, value = SirenOneOf(listOf("student", "teacher"))),
        )
    )

    fun getRemoveUserFromClassroom(orgId: Int, classroomNumber: Int) = SirenAction(
        name = "remove-user-from-classroom",
        title = "Remove User From Classroom",
        method = HttpMethod.DELETE,
        hrefTemplate = UriTemplate("${getUsersOfClassroomUri(orgId, classroomNumber)}/{$USER_PARAM}"),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "userId", type = SirenFieldType.number)
        )
    )

    // Teams

    fun getAddUserToTeam(orgId: Int, classroomNumber: Int, teamNumber: Int) = SirenAction(
        name = "add-user-to-team",
        title = "Add User To Team",
        method = HttpMethod.PUT,
        hrefTemplate = UriTemplate("${getUsersOfTeamUri(orgId, classroomNumber, teamNumber)}/{$USER_PARAM}"),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "teamNumber", type = SirenFieldType.hidden, value = teamNumber),
            SirenActionField(name = "userId", type = SirenFieldType.number)
        )
    )

    fun getRemoveUserFromTeam(orgId: Int, classroomNumber: Int, teamNumber: Int) = SirenAction(
        name = "remove-user-from-team",
        title = "Remove User From Team",
        method = HttpMethod.DELETE,
        hrefTemplate = UriTemplate("${getUsersOfTeamUri(orgId, classroomNumber, teamNumber)}/{$USER_PARAM}"),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "teamNumber", type = SirenFieldType.hidden, value = teamNumber),
            SirenActionField(name = "userId", type = SirenFieldType.number)
        )
    )
}