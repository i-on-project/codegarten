package org.ionproject.codegarten.remote.github.responses

data class GitHubTeamResponse(
    val id: Int,
    val name: String,
    val html_url: String,
    val members_count: Int,
    val organization: GitHubOrganizationResponse
)