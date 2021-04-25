package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass.assignment
import org.ionproject.codegarten.responses.siren.SirenClass.collection

class AssignmentOutputModel(
    val id: Int,
    val name: String,
    val description: String?,
    val type: String,
    val repoPrefix: String,
    val repoTemplate: String,
    val classroom: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(assignment)
}

class AssignmentsOutputModel(
    val collectionSize: Int,
    val pageIndex: Int,
    val pageSize: Int,
) : OutputModel() {
    override fun getSirenClasses() = listOf(assignment, collection)
}

data class AssignmentCreateInputModel(
    val name: String?,
    val description: String?,
    val type: String?,
    val repoPrefix: String?,
    val repoTemplate: String?,
)

data class AssignmentEditInputModel(
    val name: String?,
    val description: String?
)