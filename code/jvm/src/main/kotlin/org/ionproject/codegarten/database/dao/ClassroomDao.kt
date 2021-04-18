package org.ionproject.codegarten.database.dao

data class ClassroomDao(
    val cid: Int,
    val org_id: Int,
    val name: String,
    val description: String?,
)