package org.ionproject.codegarten.database.dao

import java.time.LocalDateTime

data class AccessTokenDao(
    val token: String,
    val expiration_date: LocalDateTime,
    val user_id: Int,
    val client_id: Int,
)
