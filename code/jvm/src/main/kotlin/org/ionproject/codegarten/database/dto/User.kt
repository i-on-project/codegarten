package org.ionproject.codegarten.database.dto

data class User(
    val uid: Int,
    val name: String,
    val gh_id: Int,
    val gh_token: String,
)
