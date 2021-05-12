package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass.assignmentInvitation
import org.ionproject.codegarten.responses.siren.SirenClass.classroomInvitation

data class ClassroomInvitationOutputModel(
    val id: Int,
    val name: String,
    val description: String?,
    val organization: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(classroomInvitation)
}

data class AssignmentInvitationOutputModel(
    val id: Int,
    val name: String,
    val description: String?,
    val type: String,
    val classroom: String,
    val organization: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(assignmentInvitation)
}

data class UserInvitationInputModel(
    val teamId: Int?,
)