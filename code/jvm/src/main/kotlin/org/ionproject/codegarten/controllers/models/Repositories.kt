package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass

class RepositoryOutputModel(
    val id: Int,
    val name: String,
    val description: String?,
    val isPrivate: Boolean,
    val organization: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(SirenClass.repository)
}

class RepositoriesOutputModel(
    val organization: String,
    val collectionSize: Int
) : OutputModel() {
    override fun getSirenClasses() = listOf(SirenClass.repository, SirenClass.collection)
}