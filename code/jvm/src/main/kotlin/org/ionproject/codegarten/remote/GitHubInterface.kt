package org.ionproject.codegarten.remote

import org.ionproject.codegarten.remote.dto.GitHubUser
import org.springframework.web.util.UriTemplate
import java.net.URI

private const val GITHUB_AUTH_ENDPOINT = "https://github.com/login/oauth/authorize"

class GitHubInterface(
    val clientId: String,
    val clientSecret: String
) {

    val endpoint = UriTemplate("$GITHUB_AUTH_ENDPOINT?client_id={clientId}&state={state}&response_type=code")

    fun getAuthEndpoint(state: String): URI {
        return endpoint.expand(clientId, state)
    }

    fun getUserFromAuthCode(authCode: String): GitHubUser {
        TODO("HTTP Request to token endpoint")
    }
}