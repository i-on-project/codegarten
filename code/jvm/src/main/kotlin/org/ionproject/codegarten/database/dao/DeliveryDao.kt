package org.ionproject.codegarten.database.dao

import java.sql.Timestamp

data class DeliveryDao(
    val tag: String,
    val due_date: Timestamp,

    val assignment_id: Int,
    val assignment_name: String,
)
