package org.ionproject.codegarten.remote

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.ionproject.codegarten.exceptions.HttpRequestException
import org.ionproject.codegarten.remote.GitHubRoutes.GITHUB_TOKEN_URI
import org.ionproject.codegarten.remote.GitHubRoutes.GITHUB_USER_URI
import org.ionproject.codegarten.remote.GitHubRoutes.getGitHubAuthUri
import org.ionproject.codegarten.remote.GitHubRoutes.getGitHubInstallationAccessTokenUri
import org.ionproject.codegarten.remote.GitHubRoutes.getGitHubInstallationUri
import org.ionproject.codegarten.remote.GitHubRoutes.getGitHubMembershipUri
import org.ionproject.codegarten.remote.GitHubRoutes.getGitHubNewInstallationUri
import org.ionproject.codegarten.remote.GitHubRoutes.getGitHubUserByIdUri
import org.ionproject.codegarten.remote.responses.GitHubInstallationAccessTokenResponse
import org.ionproject.codegarten.remote.responses.GitHubInstallationResponse
import org.ionproject.codegarten.remote.responses.GitHubOrgMembershipResponse
import org.ionproject.codegarten.remote.responses.GitHubUserAccessTokenResponse
import org.ionproject.codegarten.remote.responses.GitHubUserResponse
import java.security.Key
import java.time.Instant

class GitHubInterface(
    val appId: Int,
    val clientId: String,
    val clientName: String,
    val clientSecret: String,
    val clientPrivateKey: Key,
    val mapper: ObjectMapper
) {

    private val httpClient = OkHttpClient()

    fun getAuthUri(state: String) = getGitHubAuthUri(clientId, state)
    fun getInstallationUri() = getGitHubNewInstallationUri(clientName.toLowerCase().replace(' ', '-'))

    private fun getGitHubAppJwt(): String {
        val currTimeInSeconds = Instant.now().epochSecond

        return Jwts.builder()
            .claim("iss", appId)
            .claim("iat", currTimeInSeconds - 60)
            .claim("exp", currTimeInSeconds + (10 * 60))
            .signWith(clientPrivateKey, SignatureAlgorithm.RS256)
            .compact()
    }

    fun getAccessTokenFromAuthCode(authCode: String): GitHubUserAccessTokenResponse {
        val req = Request.Builder()
            .from(GITHUB_TOKEN_URI, clientName)
            .post(
                FormBody.Builder()
                    .add("code", authCode)
                    .add("client_id", clientId)
                    .add("client_secret", clientSecret)
                    .build()
            )
            .build()

        return httpClient.callAndMap(req, mapper, GitHubUserAccessTokenResponse::class.java)
    }

    fun getUserInfo(accessToken: String): GitHubUserResponse {
        val req = Request.Builder()
            .from(GITHUB_USER_URI, clientName, accessToken)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubUserResponse::class.java)
    }

    fun getInstallationOrg(installationId: Int): GitHubInstallationResponse {
        val gitHubAppJwt = getGitHubAppJwt()
        val req = Request.Builder()
            .from(getGitHubInstallationUri(installationId), clientName, gitHubAppJwt)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubInstallationResponse::class.java)
    }

    fun getInstallationToken(installationId: Int): GitHubInstallationAccessTokenResponse {
        val gitHubAppJwt = getGitHubAppJwt()
        val req = Request.Builder()
            .from(getGitHubInstallationAccessTokenUri(installationId), clientName, gitHubAppJwt)
            .post(FormBody.Builder().build())
            .build()

        return httpClient.callAndMap(req, mapper, GitHubInstallationAccessTokenResponse::class.java)
    }

    fun getUser(userId: Int): GitHubUserResponse {
        val req = Request.Builder()
            .from(getGitHubUserByIdUri(userId), clientName)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubUserResponse::class.java)
    }

    fun getMembership(orgId: Int, userId: Int, accessToken: String): GitHubOrgMembershipResponse {
        val username = getUser(userId).login
        val req = Request.Builder()
            .from(getGitHubMembershipUri(orgId, username), clientName, accessToken)
            .build()

        try {
            return httpClient.callAndMap(req, mapper, GitHubOrgMembershipResponse::class.java)
        } catch (ex: HttpRequestException) {
            throw AuthorizationException("Error getting user membership")
        }

    }
}