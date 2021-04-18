package org.ionproject.codegarten.database.dao

data class AssignmentDao(
    val aid: Int,
    val name: String,
    val description: String?,
    val type: String,
    val repo_prefix: String,
    val template: String?,

    val classroom_id: Int,
    val classroom_name: String,
)