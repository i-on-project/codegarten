package org.ionproject.codegarten.controllers.models

import org.ionproject.codegarten.responses.siren.SirenClass.collection
import org.ionproject.codegarten.responses.siren.SirenClass.user

class UserOutputModel(
    val id: Int,
    val name: String,
    val gitHubId: Int,
    val gitHubName: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(user)
}

class UserClassroomOutputModel(
    val id: Int,
    val name: String,
    val gitHubId: Int,
    val role: String
) : OutputModel() {
    override fun getSirenClasses() = listOf(user)
}

class UserAssignmentOutputModel(
    val id: Int,
    val name: String,
    val gitHubId: Int,
    val repoId: Int
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


val validRoleTypes = listOf("student", "teacher")
data class UserAddInputModel(
    val role: String?
)

data class UserEditInputModel(
    val name: String?
)