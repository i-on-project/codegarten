package org.ionproject.codegarten.database.dto

import java.time.OffsetDateTime

data class AccessToken(
    val token: String,
    val expiration_date: OffsetDateTime,
    val user_id: Int,
    val client_id: Int,
)
