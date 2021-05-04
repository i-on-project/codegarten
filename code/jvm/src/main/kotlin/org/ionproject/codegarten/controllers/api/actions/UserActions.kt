package org.ionproject.codegarten.controllers.api.actions

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.Routes.USER_HREF
import org.ionproject.codegarten.Routes.USER_PARAM
import org.ionproject.codegarten.Routes.getClassroomByNumberUri
import org.ionproject.codegarten.Routes.includeHost
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
        href = URI(USER_HREF).includeHost(),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "name", type = SirenFieldType.text),
        )
    )

    fun getDeleteUserAction() = SirenAction(
        name = "delete-user",
        title = "Delete User",
        method = HttpMethod.DELETE,
        href = URI(USER_HREF).includeHost()
    )

    fun getAddUserToClassroom(orgId: Int, classroomNumber: Int) = SirenAction(
        name = "add-user-to-classroom",
        title = "Add User To Classroom",
        method = HttpMethod.PUT,
        hrefTemplate = UriTemplate("${getClassroomByNumberUri(orgId, classroomNumber)}/{$USER_PARAM}").includeHost(),
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
        hrefTemplate = UriTemplate("${getClassroomByNumberUri(orgId, classroomNumber)}/{$USER_PARAM}").includeHost(),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "userId", type = SirenFieldType.number)
        )
    )
}