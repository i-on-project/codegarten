package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass.collection
import org.ionproject.codegarten.responses.siren.SirenClass.user

class UserOutputModel(
    val id: Int,
    val name: String,
    val githubName: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(user)
}

class UsersOutputModel(
    val collectionSize: Int,
    val pageIndex: Int,
    val pageSize: Int,
) : OutputModel() {
    override fun getSirenClasses() = listOf(user, collection)
}

data class UserAddInputModel(
    val id: Int?
)

data class UserEditInputModel(
    val name: String?
)