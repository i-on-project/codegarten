package org.ionproject.codegarten.controllers.models

data class AuthorizationInputModel (
    val code: String?,
    val client_id: String?,
    val client_secret: String?,
    val state: String?
)