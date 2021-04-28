package org.ionproject.codegarten.database.dto

import java.time.OffsetDateTime

data class Installation(
    val iid: Int,
    val org_id: Int,
    val accessToken: String,
    val expiration_date: OffsetDateTime
)
