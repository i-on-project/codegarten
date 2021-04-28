package org.ionproject.codegarten.remote.responses

import java.time.OffsetDateTime

data class GitHubInstallationAccessTokenResponse(
    val token: String,
    val expires_at: OffsetDateTime
)
