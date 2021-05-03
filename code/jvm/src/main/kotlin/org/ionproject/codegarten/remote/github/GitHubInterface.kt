package org.ionproject.codegarten.remote.github

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.ionproject.codegarten.exceptions.HttpRequestException
import org.ionproject.codegarten.remote.callAndMap
import org.ionproject.codegarten.remote.callAndMapList
import org.ionproject.codegarten.remote.from
import org.ionproject.codegarten.remote.github.GitHubRoutes.GITHUB_TOKEN_URI
import org.ionproject.codegarten.remote.github.GitHubRoutes.GITHUB_USER_URI
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubAuthUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubInstallationAccessTokenUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubInstallationOfOrgUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubInstallationUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubMembershipUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubNewInstallationUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubOrgUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubRepoByIdUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubRepoByNameUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubUserByIdUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGithubUserOrgsUri
import org.ionproject.codegarten.remote.github.responses.GitHubInstallationAccessTokenResponse
import org.ionproject.codegarten.remote.github.responses.GitHubInstallationResponse
import org.ionproject.codegarten.remote.github.responses.GitHubLoginResponse
import org.ionproject.codegarten.remote.github.responses.GitHubOrgMembershipResponse
import org.ionproject.codegarten.remote.github.responses.GitHubOrganizationResponse
import org.ionproject.codegarten.remote.github.responses.GitHubRepoResponse
import org.ionproject.codegarten.remote.github.responses.GitHubUserAccessTokenResponse
import org.ionproject.codegarten.remote.github.responses.GitHubUserOrgRole.NOT_A_MEMBER
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

    fun getUserInfo(accessToken: String): GitHubLoginResponse {
        val req = Request.Builder()
            .from(GITHUB_USER_URI, clientName, accessToken)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubLoginResponse::class.java)
    }

    fun getInstallationOrg(installationId: Int): GitHubInstallationResponse {
        val gitHubAppJwt = getGitHubAppJwt()
        val req = Request.Builder()
            .from(getGitHubInstallationUri(installationId), clientName, gitHubAppJwt)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubInstallationResponse::class.java)
    }

    fun getOrgInstallation(orgId: Int): GitHubInstallationResponse {
        val gitHubAppJwt = getGitHubAppJwt()
        val req = Request.Builder()
            .from(getGitHubInstallationOfOrgUri(orgId), clientName, gitHubAppJwt)
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

    fun getUser(userId: Int, accessToken: String): GitHubLoginResponse {
        val req = Request.Builder()
            .from(getGitHubUserByIdUri(userId), clientName, accessToken)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubLoginResponse::class.java)
    }

    fun getUserOrgMembership(orgId: Int, accessToken: String): GitHubOrgMembershipResponse {
        val req = Request.Builder()
            .from(getGitHubMembershipUri(orgId), clientName, accessToken)
            .build()

        return try {
            httpClient.callAndMap(req, mapper, GitHubOrgMembershipResponse::class.java)
        } catch (ex: HttpRequestException) {
            GitHubOrgMembershipResponse(NOT_A_MEMBER)
        }
    }

    fun getUserOrgs(accessToken: String, page: Int, limit: Int): List<GitHubOrganizationResponse> {
        val req = Request.Builder()
            .from(getGithubUserOrgsUri(page, limit), clientName, accessToken)
            .build()

        return httpClient.callAndMapList(req, mapper, GitHubOrganizationResponse::class.java)
    }

    fun getOrgById(orgId: Int, accessToken: String): GitHubOrganizationResponse {
        val req = Request.Builder()
            .from(getGitHubOrgUri(orgId), clientName, accessToken)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubOrganizationResponse::class.java)
    }

    fun getRepoById(repoId: Int, accessToken: String): GitHubRepoResponse {
        val req = Request.Builder()
            .from(getGitHubRepoByIdUri(repoId), clientName, accessToken)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubRepoResponse::class.java)
    }

    fun getRepoByName(login: String, repoName: String, accessToken: String): GitHubRepoResponse {
        val req = Request.Builder()
            .from(getGitHubRepoByNameUri(login, repoName), clientName, accessToken)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubRepoResponse::class.java)
    }
}