package org.ionproject.codegarten.remote.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubOrgMembershipResponse (
    val role: String,
    val user: GitHubUserResponse,
)

enum class GitHubUserOrgRole {
    @JsonProperty("admin") ADMIN,
    @JsonProperty("member") MEMBER;
}