package org.ionproject.codegarten.controllers.api.actions

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.responses.siren.SirenAction
import org.ionproject.codegarten.responses.siren.SirenActionField
import org.ionproject.codegarten.responses.siren.SirenFieldType
import org.ionproject.codegarten.responses.siren.SirenOneOf
import org.springframework.http.HttpMethod

object AssignmentActions {

    fun getCreateAssignmentAction(orgId: Int, classroomNumber: Int) = SirenAction(
        name = "create-assignment",
        title = "Create Assignment",
        method = HttpMethod.POST,
        href = Routes.getAssignmentsUri(orgId, classroomNumber).includeHost(),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "name", type = SirenFieldType.text),
            SirenActionField(name = "description", type = SirenFieldType.text),
            SirenActionField(name = "type", type = SirenFieldType.text, value = SirenOneOf(listOf("individual", "group"))),
            SirenActionField(name = "repoPrefix", type = SirenFieldType.text),
            SirenActionField(name = "repoTemplate", type = SirenFieldType.text)
        )
    )

    fun getEditAssignmentAction(orgId: Int, classroomNumber: Int, assignmentNumber: Int) = SirenAction(
        name = "edit-assignment",
        title = "Edit Assignment",
        method = HttpMethod.PUT,
        href = Routes.getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost(),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "assignmentNumber", type = SirenFieldType.hidden, value = assignmentNumber),
            SirenActionField(name = "name", type = SirenFieldType.text),
            SirenActionField(name = "description", type = SirenFieldType.text)
        )
    )

    fun getDeleteAssignmentAction(orgId: Int, classroomNumber: Int, assignmentNumber: Int) = SirenAction(
        name = "delete-assignment",
        title = "Delete Assignment",
        method = HttpMethod.DELETE,
        href = Routes.getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost(),
        type = Routes.INPUT_CONTENT_TYPE,
        fields = listOf(
            SirenActionField(name = "orgId", type = SirenFieldType.hidden, value = orgId),
            SirenActionField(name = "classroomNumber", type = SirenFieldType.hidden, value = classroomNumber),
            SirenActionField(name = "assignmentNumber", type = SirenFieldType.hidden, value = assignmentNumber)
        )
    )
}