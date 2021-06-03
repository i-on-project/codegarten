package org.ionproject.codegarten.database.dto

data class UserAssignment(
    val uid: Int,
    val name: String,
    val gh_id: Int,
    val gh_token: String,
    val repo_id: Int,

    val count: Int? = null
)
