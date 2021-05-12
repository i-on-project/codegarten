package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass.classroom
import org.ionproject.codegarten.responses.siren.SirenClass.collection

class ClassroomOutputModel(
    val id: Int,
    val inviteCode: String?,
    val number: Int,
    val name: String,
    val description: String?,
    val organization: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(classroom)
}

class ClassroomsOutputModel(
    val collectionSize: Int,
    val pageIndex: Int,
    val pageSize: Int,
) : OutputModel() {
    override fun getSirenClasses() = listOf(classroom, collection)
}

data class ClassroomCreateInputModel(
    val name: String?,
    val description: String?
)

data class ClassroomEditInputModel(
    val name: String?,
    val description: String?
)