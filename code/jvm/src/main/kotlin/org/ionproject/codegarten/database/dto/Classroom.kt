package org.ionproject.codegarten.database.dto

data class Classroom(
    val cid: Int,
    val number: Int,
    val inv_code: String?,
    val org_id: Int,
    val name: String,
    val description: String?,
)