package org.ionproject.codegarten.remote

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.ionproject.codegarten.remote.dto.GitHubUser
import org.ionproject.codegarten.remote.responses.GitHubAccessTokenResponse
import org.ionproject.codegarten.remote.responses.GitHubUserResponse
import org.springframework.web.util.UriTemplate
import java.net.URI

private const val GITHUB_AUTH_ENDPOINT = "https://github.com/login/oauth/authorize"
private const val GITHUB_TOKEN_ENDPOINT = "https://github.com/login/oauth/access_token"

private const val GITHUB_API_HOST = "https://api.github.com"
private const val GITHUB_USER_ENDPOINT = "user"
private const val GITHUB_ORGANIZATIONS_ENDPOINT = "user/orgs"

class GitHubInterface(
    val clientId: String,
    val clientSecret: String,
    val mapper: ObjectMapper
) {

    private val authEndpoint = UriTemplate("$GITHUB_AUTH_ENDPOINT?client_id={clientId}&state={state}&response_type=code")
    private val httpClient = OkHttpClient()

    private fun getRequestBuilder(url: String, token: String? = null): Request.Builder {
        val toReturn = Request.Builder().url(url).addHeader("Accept", "application/json")
        if (token != null) toReturn.addHeader("Authorization", "token $token")
        return toReturn
    }

    fun getAuthEndpoint(state: String): URI {
        return authEndpoint.expand(clientId, state)
    }

    fun getUserFromAuthCode(authCode: String): GitHubUser {
        val req = getRequestBuilder(GITHUB_TOKEN_ENDPOINT)
            .post(
                FormBody.Builder()
                    .add("code", authCode)
                    .add("client_id", clientId)
                    .add("client_secret", clientSecret)
                    .build()
            )
            .build()

        val resBody = httpClient.newCall(req).execute().body!!.string()
        val accessTokenResponse = mapper.readValue(resBody, GitHubAccessTokenResponse::class.java)

        return getUserInfo(accessTokenResponse.access_token)
    }

    fun getUserInfo(accessToken: String): GitHubUser {
        val req = getRequestBuilder("$GITHUB_API_HOST/$GITHUB_USER_ENDPOINT", accessToken).build()

        val resBody = httpClient.newCall(req).execute().body!!.string()
        val userResponse = mapper.readValue(resBody, GitHubUserResponse::class.java)

        return GitHubUser(
            userResponse.id,
            userResponse.login,
            accessToken
        )
    }
}