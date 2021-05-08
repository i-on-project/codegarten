package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass.collection
import org.ionproject.codegarten.responses.siren.SirenClass.team

class TeamOutputModel(
    val id: Int,
    val number: Int,
    val name: String,
    val classroom: String,
    val organization: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(team)
}

class TeamsOutputModel(
    val collectionSize: Int,
    val pageIndex: Int,
    val pageSize: Int,
) : OutputModel() {
    override fun getSirenClasses() = listOf(team, collection)
}

data class TeamCreateInputModel(
    val name: String?
)

data class TeamEditInputModel(
    val name: String?
)