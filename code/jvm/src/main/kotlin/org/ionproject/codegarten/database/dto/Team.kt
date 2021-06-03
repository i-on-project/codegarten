package org.ionproject.codegarten.database.dto

data class Team(
    val tid: Int,
    val number: Int,
    val name: String,
    val gh_id: Int,

    val org_id: Int,
    val classroom_id: Int,
    val classroom_number: Int,
    val classroom_name: String,

    val count: Int? = null
)

data class CreatedTeam(
    val tid: Int,
    val cid: Int,
    val number: Int,
    val name: String,
    val gh_id: Int,
)
