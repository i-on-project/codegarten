package org.ionproject.codegarten.controllers.api.actions

import org.ionproject.codegarten.Routes.INPUT_CONTENT_TYPE
import org.ionproject.codegarten.Routes.getClassroomByNumberUri
import org.ionproject.codegarten.Routes.getClassroomsUri
import org.ionproject.codegarten.responses.siren.SirenAction
import org.ionproject.codegarten.responses.siren.SirenActionField
import org.ionproject.codegarten.responses.siren.SirenFieldType.hidden
import org.ionproject.codegarten.responses.siren.SirenFieldType.text
import org.springframework.http.HttpMethod

object ClassroomActions {

    fun getCreateClassroomAction(orgId: Int) = SirenAction(
        name = "create-classroom",
        title = "Create Classroom",
        method = HttpMethod.POST,
        href = getClassroomsUri(orgId),
        type = INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = hidden, value = orgId),
            SirenActionField(name = "name", type = text),
            SirenActionField(name = "description", type = text)
        )
    )

    fun getEditClassroomAction(orgId: Int, classroomNumber: Int) = SirenAction(
        name = "edit-classroom",
        title = "Edit Classroom",
        method = HttpMethod.PUT,
        href = getClassroomByNumberUri(orgId, classroomNumber),
        type = INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = hidden, value = classroomNumber),
            SirenActionField(name = "name", type = text),
            SirenActionField(name = "description", type = text)
        )
    )

    fun getDeleteClassroomAction(orgId: Int, classroomNumber: Int) = SirenAction(
        name = "delete-classroom",
        title = "Delete Classroom",
        method = HttpMethod.DELETE,
        href = getClassroomByNumberUri(orgId, classroomNumber),
        type = INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = hidden, value = classroomNumber)
        )
    )
}