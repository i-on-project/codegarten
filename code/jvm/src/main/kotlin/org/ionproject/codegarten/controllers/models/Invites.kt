package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass.assignmentInvitation
import org.ionproject.codegarten.responses.siren.SirenClass.classroomInvitation

data class ClassroomInvitationOutputModel(
    val id: Int,
    val number: Int,
    val name: String,
    val description: String?,

    val orgId: Int,
    val organization: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(classroomInvitation)
}

data class AssignmentInvitationOutputModel(
    val id: Int,
    val number: Int,
    val name: String,
    val description: String?,
    val type: String,

    val classroomId: Int,
    val classroomNumber: Int,
    val classroom: String,

    val orgId: Int,
    val organization: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(assignmentInvitation)
}

data class UserInvitationInputModel(
    val teamId: Int?,
)