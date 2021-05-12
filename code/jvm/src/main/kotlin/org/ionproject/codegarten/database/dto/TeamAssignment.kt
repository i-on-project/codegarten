package org.ionproject.codegarten.database.dto

data class TeamAssignment(
    val tid: Int,
    val number: Int,
    val name: String,
    val gh_id: Int,
    val repo_id: Int,
)
