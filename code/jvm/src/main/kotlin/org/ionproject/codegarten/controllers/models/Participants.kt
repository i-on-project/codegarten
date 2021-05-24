package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass.collection
import org.ionproject.codegarten.responses.siren.SirenClass.participant

class ParticipantOutputModel(
    val type: String,
    val id: Int,
    val name: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(participant)
}

class ParticipantItemOutputModel(
    val id: Int,
    val name: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(participant)
}

enum class ParticipantTypes(val type: String) {
    USER("user"),
    TEAM("team"),
    TEACHER("teacher")
}

class ParticipantsOutputModel(
    val participantsType: String,
    val collectionSize: Int,
    val pageIndex: Int,
    val pageSize: Int,
) : OutputModel() {
    override fun getSirenClasses() = listOf(participant, collection)
}