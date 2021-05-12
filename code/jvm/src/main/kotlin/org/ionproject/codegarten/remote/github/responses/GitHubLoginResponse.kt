package org.ionproject.codegarten.remote.github.responses

data class GitHubLoginResponse(
    val id: Int,
    val login: String,
    val avatar_url: String,
)