package org.ionproject.codegarten.controllers.models

data class AuthorizationInputModel (
    val code: String?,
    val client_id: Int?,
    val client_secret: String?
)