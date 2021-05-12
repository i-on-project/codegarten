package org.ionproject.codegarten.remote.github.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubInstallationResponse(
    val id: Int,
    val account: GitHubAccountResponse
)

data class GitHubAccountResponse(
    val login: String,
    val id: Int,
    val type: GitHubAccountType,
)

enum class GitHubAccountType {
    @JsonProperty("Organization") ORGANIZATION,
    @JsonProperty("User") USER;
}