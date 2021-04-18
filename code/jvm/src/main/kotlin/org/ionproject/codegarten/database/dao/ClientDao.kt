package org.ionproject.codegarten.database.dao

data class ClientDao(
    val cid: Int,
    val name: String,
    val secret: String,
    val redirect_uri: String,
)
