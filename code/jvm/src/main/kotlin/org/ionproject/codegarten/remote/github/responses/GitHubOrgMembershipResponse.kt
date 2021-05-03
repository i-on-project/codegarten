package org.ionproject.codegarten.remote.github.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubOrgMembershipResponse (
    val role: GitHubUserOrgRole,
    val user: GitHubLoginResponse? = null,
)

enum class GitHubUserOrgRole {
    @JsonProperty("admin") ADMIN,
    @JsonProperty("member") MEMBER,
    NOT_A_MEMBER
}