package org.ionproject.codegarten.database.dao

import java.time.LocalDateTime

data class AuthCodeDao(
    val code: String,
    val expiration_date: LocalDateTime,
    val user_id: Int,
    val client_id: Int,
)
