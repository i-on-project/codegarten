package org.ionproject.codegarten.remote.dto

data class GitHubUser(
    val userId: Int,
    val username: String,
    val accessToken: String
)
