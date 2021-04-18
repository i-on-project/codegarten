package org.ionproject.codegarten.database.dao

import java.sql.Timestamp

data class AccessTokenDao(
    val token: String,
    val expiration_date: Timestamp,
    val user_id: Int,
    val client_id: Int,
)
