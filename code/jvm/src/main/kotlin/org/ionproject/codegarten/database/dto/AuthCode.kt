package org.ionproject.codegarten.database.dto

import java.time.OffsetDateTime

data class AuthCode(
    val code: String,
    val expiration_date: OffsetDateTime,
    val user_id: Int,
    val client_id: Int,
)
