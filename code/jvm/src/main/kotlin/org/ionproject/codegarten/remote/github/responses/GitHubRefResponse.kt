package org.ionproject.codegarten.remote.github.responses

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class GitHubTagResponse(
    val tag: String,
    @JsonProperty("object") val obj: GitHubRefObjectResponse
)

data class GitHubCommitResponse(
    val author: GitHubCommitAuthorResponse
)

data class GitHubCommitAuthorResponse(
    val date: OffsetDateTime
)

data class GitHubRefResponse(
    val ref: String,
    @JsonProperty("object") val obj: GitHubRefObjectResponse
)

data class GitHubRefObjectResponse(
    val type: String,
    val url: String
)

data class GitHubTag(
    val name: String,
    val date: OffsetDateTime? = null,
)
