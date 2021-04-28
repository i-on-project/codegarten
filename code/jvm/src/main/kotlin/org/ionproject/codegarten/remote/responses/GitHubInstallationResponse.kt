package org.ionproject.codegarten.remote.responses

import org.ionproject.codegarten.remote.responses.GitHubAccountType.ORGANIZATION

data class GitHubInstallationResponse(
    val id: Int,
    val account: GitHubAccountResponse
)

data class GitHubAccountResponse(
    val login: String,
    val id: Int,
    val type: String,
)

enum class GitHubAccountType(val type: String) {
    ORGANIZATION("Organization"),
    USER("User")
}

fun GitHubAccountResponse.isOrganization() = ORGANIZATION.type == this.type