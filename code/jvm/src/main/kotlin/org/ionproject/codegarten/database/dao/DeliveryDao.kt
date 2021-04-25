package org.ionproject.codegarten.database.dao

import java.time.LocalDateTime

data class DeliveryDao(
    val tag: String,
    val due_date: LocalDateTime,

    val assignment_id: Int,
    val assignment_name: String,
)
