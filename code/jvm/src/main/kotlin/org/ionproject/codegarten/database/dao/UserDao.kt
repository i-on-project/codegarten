package org.ionproject.codegarten.database.dao

data class UserDao(
    val uid: Int,
    val name: String,
    val gh_id: String,
    val gh_token: String,
)
