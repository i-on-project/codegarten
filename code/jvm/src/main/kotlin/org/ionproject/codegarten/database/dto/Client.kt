package org.ionproject.codegarten.database.dto

data class Client(
    val cid: Int,
    val name: String,
    val secret: String,
    val redirect_uri: String,
)
