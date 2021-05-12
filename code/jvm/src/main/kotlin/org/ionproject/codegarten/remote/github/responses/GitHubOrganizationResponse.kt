package org.ionproject.codegarten.remote.github.responses

data class GitHubOrganizationResponse(
    val id: Int,
    val login: String,
    val description: String?,
    val avatar_url: String
)