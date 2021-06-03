package org.ionproject.codegarten.database.dto

import java.time.OffsetDateTime

data class Delivery(
    val did: Int,
    val number: Int,
    val tag: String,
    val due_date: OffsetDateTime?,

    val assignment_id: Int,
    val assignment_number: Int,
    val assignment_name: String,

    val org_id: Int,
    val classroom_id: Int,
    val classroom_number: Int,
    val classroom_name: String,

    val count: Int? = null
)

data class CreatedDelivery(
    val did: Int,
    val aid: Int,
    val number: Int,
    val tag: String,
    val due_date: OffsetDateTime?,
)
