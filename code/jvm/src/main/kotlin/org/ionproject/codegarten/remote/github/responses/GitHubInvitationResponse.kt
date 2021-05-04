package org.ionproject.codegarten.remote.github.responses

data class GitHubInvitationResponse(
    val id: Int,
    val repository: GitHubRepoResponse
)