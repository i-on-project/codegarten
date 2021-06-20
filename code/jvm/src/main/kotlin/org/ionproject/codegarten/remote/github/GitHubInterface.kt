package org.ionproject.codegarten.remote.github

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.ionproject.codegarten.GitHubAppProperties
import org.ionproject.codegarten.exceptions.HttpRequestException
import org.ionproject.codegarten.remote.MEDIA_TYPE_JSON
import org.ionproject.codegarten.remote.call
import org.ionproject.codegarten.remote.callAndMap
import org.ionproject.codegarten.remote.callAndMapList
import org.ionproject.codegarten.remote.from
import org.ionproject.codegarten.remote.github.GitHubCacheTimes.ORG_INFO_CACHE
import org.ionproject.codegarten.remote.github.GitHubCacheTimes.ORG_MEMBERSHIP_CACHE
import org.ionproject.codegarten.remote.github.GitHubCacheTimes.REPO_INFO_CACHE
import org.ionproject.codegarten.remote.github.GitHubCacheTimes.REPO_SEARCH_CACHE
import org.ionproject.codegarten.remote.github.GitHubCacheTimes.REPO_TAG_CACHE
import org.ionproject.codegarten.remote.github.GitHubCacheTimes.TEAM_INFO_CACHE
import org.ionproject.codegarten.remote.github.GitHubCacheTimes.USER_INFO_CACHE
import org.ionproject.codegarten.remote.github.GitHubCacheTimes.USER_ORGS_CACHE
import org.ionproject.codegarten.remote.github.GitHubRoutes.GITHUB_TOKEN_URI
import org.ionproject.codegarten.remote.github.GitHubRoutes.GITHUB_USER_URI
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubAuthUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubInstallationAccessTokenUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubInstallationOfOrgUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubInstallationUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubMembershipUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubNewInstallationUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubOrgInvitationsUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubOrgUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubRefTagUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubRefsTagsUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubRepoByIdUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubRepoByNameUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubRepoCollaboratorUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubRepoGenerateByIdUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubRepoReleasesByIdUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubReposOfOrgUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubTagNameFromRef
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubTeamByIdUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubTeamRepoUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubTeamUserMembershipUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubTeamsUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubUserByIdUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGithubUserOrgsUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.searchGitHubReposInOrgUri
import org.ionproject.codegarten.remote.github.responses.GitHubCommitResponse
import org.ionproject.codegarten.remote.github.responses.GitHubInstallationAccessTokenResponse
import org.ionproject.codegarten.remote.github.responses.GitHubInstallationResponse
import org.ionproject.codegarten.remote.github.responses.GitHubLoginResponse
import org.ionproject.codegarten.remote.github.responses.GitHubOrgMembershipResponse
import org.ionproject.codegarten.remote.github.responses.GitHubOrganizationResponse
import org.ionproject.codegarten.remote.github.responses.GitHubRefResponse
import org.ionproject.codegarten.remote.github.responses.GitHubRepoResponse
import org.ionproject.codegarten.remote.github.responses.GitHubRepoSearchResponse
import org.ionproject.codegarten.remote.github.responses.GitHubTag
import org.ionproject.codegarten.remote.github.responses.GitHubTagResponse
import org.ionproject.codegarten.remote.github.responses.GitHubTeamResponse
import org.ionproject.codegarten.remote.github.responses.GitHubUserAccessTokenResponse
import org.ionproject.codegarten.remote.github.responses.GitHubUserOrgRole.NOT_A_MEMBER
import org.springframework.http.HttpStatus
import java.security.Key
import java.time.Instant
import java.util.*

class GitHubInterface(
    val ghAppProperties: GitHubAppProperties,
    val clientPrivateKey: Key,
    val mapper: ObjectMapper
) {

    private val httpClient = OkHttpClient()

    fun getAuthUri(state: String) = getGitHubAuthUri(ghAppProperties.clientId, state)
    fun getInstallationUri() = getGitHubNewInstallationUri(ghAppProperties.name.toLowerCase().replace(' ', '-'))

    private fun getGitHubAppJwt(): String {
        val currTimeInSeconds = Instant.now().epochSecond

        return Jwts.builder()
            .claim("iss", ghAppProperties.id)
            .claim("iat", currTimeInSeconds - 60)
            .claim("exp", currTimeInSeconds + (10 * 60))
            .signWith(clientPrivateKey, SignatureAlgorithm.RS256)
            .compact()
    }

    fun getAccessTokenFromAuthCode(authCode: String): GitHubUserAccessTokenResponse {
        val req = Request.Builder()
            .from(GITHUB_TOKEN_URI, ghAppProperties.name)
            .post(
                FormBody.Builder()
                    .add("code", authCode)
                    .add("client_id", ghAppProperties.clientId)
                    .add("client_secret", ghAppProperties.clientSecret)
                    .build()
            )
            .build()

        return httpClient.callAndMap(req, mapper, GitHubUserAccessTokenResponse::class.java)
    }

    fun getUserInfo(accessToken: String): GitHubLoginResponse {
        val req = Request.Builder()
            .from(GITHUB_USER_URI, ghAppProperties.name, accessToken)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubLoginResponse::class.java, USER_INFO_CACHE)
    }

    fun getInstallationOrg(installationId: Int): GitHubInstallationResponse {
        val gitHubAppJwt = getGitHubAppJwt()
        val req = Request.Builder()
            .from(getGitHubInstallationUri(installationId), ghAppProperties.name, gitHubAppJwt)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubInstallationResponse::class.java)
    }

    fun getOrgInstallation(orgId: Int): GitHubInstallationResponse {
        val gitHubAppJwt = getGitHubAppJwt()
        val req = Request.Builder()
            .from(getGitHubInstallationOfOrgUri(orgId), ghAppProperties.name, gitHubAppJwt)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubInstallationResponse::class.java)
    }

    fun getInstallationToken(installationId: Int): GitHubInstallationAccessTokenResponse {
        val gitHubAppJwt = getGitHubAppJwt()
        val req = Request.Builder()
            .from(getGitHubInstallationAccessTokenUri(installationId), ghAppProperties.name, gitHubAppJwt)
            .post(FormBody.Builder().build())
            .build()

        return httpClient.callAndMap(req, mapper, GitHubInstallationAccessTokenResponse::class.java)
    }

    fun getUser(userId: Int, accessToken: String): GitHubLoginResponse {
        val req = Request.Builder()
            .from(getGitHubUserByIdUri(userId), ghAppProperties.name, accessToken)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubLoginResponse::class.java, USER_INFO_CACHE)
    }

    fun getUserOrgMembership(orgId: Int, accessToken: String): GitHubOrgMembershipResponse {
        val req = Request.Builder()
            .from(getGitHubMembershipUri(orgId), ghAppProperties.name, accessToken)
            .build()

        return try {
            httpClient.callAndMap(req, mapper, GitHubOrgMembershipResponse::class.java, ORG_MEMBERSHIP_CACHE)
        } catch (ex: HttpRequestException) {
            GitHubOrgMembershipResponse(NOT_A_MEMBER)
        }
    }

    fun getUserOrgs(accessToken: String, page: Int, limit: Int): List<GitHubOrganizationResponse> {
        val req = Request.Builder()
            .from(getGithubUserOrgsUri(page, limit), ghAppProperties.name, accessToken)
            .build()

        return httpClient.callAndMapList(req, mapper, GitHubOrganizationResponse::class.java, USER_ORGS_CACHE)
    }

    fun getOrgById(orgId: Int, accessToken: String): GitHubOrganizationResponse {
        val req = Request.Builder()
            .from(getGitHubOrgUri(orgId), ghAppProperties.name, accessToken)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubOrganizationResponse::class.java, ORG_INFO_CACHE)
    }

    fun getRepoById(repoId: Int, accessToken: String): GitHubRepoResponse {
        val req = Request.Builder()
            .from(getGitHubRepoByIdUri(repoId), ghAppProperties.name, accessToken)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubRepoResponse::class.java, REPO_INFO_CACHE)
    }

    fun getRepoByName(login: String, repoName: String, accessToken: String): GitHubRepoResponse {
        val req = Request.Builder()
            .from(getGitHubRepoByNameUri(login, repoName), ghAppProperties.name, accessToken)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubRepoResponse::class.java)
    }

    fun createRepo(orgId: Int, repoName: String, installationToken: String): GitHubRepoResponse {
        val json = mapper.createObjectNode()
        json.put("name", repoName)
        json.put("private", true)
        val body = mapper.writeValueAsString(json)

        val req = Request.Builder()
            .from(getGitHubReposOfOrgUri(orgId), ghAppProperties.name, installationToken)
            .post(body.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        return httpClient.callAndMap(req, mapper, GitHubRepoResponse::class.java)
    }

    fun createRepoFromTemplate(orgName: String, repoName: String, repoTemplateId: Int, installationToken: String): GitHubRepoResponse {
        val json = mapper.createObjectNode()
        json.put("name", repoName)
        json.put("private", true)
        json.put("owner", orgName)
        val body = mapper.writeValueAsString(json)

        val req = Request.Builder()
            .from(getGitHubRepoGenerateByIdUri(repoTemplateId), ghAppProperties.name, installationToken)
            .post(body.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        return httpClient.callAndMap(req, mapper, GitHubRepoResponse::class.java)
    }

    fun deleteRepo(repoId: Int, installationToken: String) {
        val req = Request.Builder()
            .from(getGitHubRepoByIdUri(repoId), ghAppProperties.name, installationToken)
            .delete()
            .build()

        return httpClient.call(req)
    }

    fun searchRepos(orgName: String, toSearch: String?, ghToken: String): GitHubRepoSearchResponse {
        val req = Request.Builder()
            .from(searchGitHubReposInOrgUri(orgName, toSearch ?: ""), ghAppProperties.name, ghToken)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubRepoSearchResponse::class.java, REPO_SEARCH_CACHE)
    }

    fun addUserToRepo(repoId: Int, username: String, installationToken: String) {
        val req = Request.Builder()
            .from(getGitHubRepoCollaboratorUri(repoId, username), ghAppProperties.name, installationToken)
            .put(FormBody.Builder().build())
            .build()

        httpClient.call(req)
    }

    fun getAllTagsFromRepo(repoId: Int, ghToken: String): List<GitHubTag> {
        val req = Request.Builder()
            .from(getGitHubRefsTagsUri(repoId), ghAppProperties.name, ghToken)
            .build()

        return try {
            val refs = httpClient.callAndMapList(req, mapper, GitHubRefResponse::class.java)
            refs.map { GitHubTag(name = getGitHubTagNameFromRef(it.ref)) }
        } catch (ex: HttpRequestException) {
            if (ex.status != HttpStatus.NOT_FOUND.value() && ex.status != HttpStatus.CONFLICT.value()) {
                throw ex
            }

            listOf()
        }
    }

    fun tryGetTagFromRepo(repoId: Int, tag: String, ghToken: String): Optional<GitHubTag> {
        var req = Request.Builder()
            .from(getGitHubRefTagUri(repoId, tag), ghAppProperties.name, ghToken)
            .build()

        return try {
            val ref = httpClient.callAndMap(req, mapper, GitHubRefResponse::class.java, REPO_TAG_CACHE)

            val commitUri =
                if (ref.obj.type == "tag") {
                    // If ref is a tag, get the tag's last commit
                    req = Request.Builder()
                        .from(ref.obj.url, ghAppProperties.name, ghToken)
                        .build()

                    val ghTag = httpClient.callAndMap(req, mapper, GitHubTagResponse::class.java, REPO_TAG_CACHE)
                    ghTag.obj.url
                } else {
                    // If ref is not tag, it's a commit
                    ref.obj.url
                }

            req = Request.Builder()
                .from(commitUri, ghAppProperties.name, ghToken)
                .build()
            val commit = httpClient.callAndMap(req, mapper, GitHubCommitResponse::class.java, REPO_TAG_CACHE)

            Optional.of(
                GitHubTag(
                    name = tag,
                    date = commit.author.date
                )
            )
        } catch (ex: HttpRequestException) {
            if (ex.status != HttpStatus.NOT_FOUND.value()) {
                throw ex
            }

            Optional.empty()
        }
    }

    fun createReleaseInRepo(repoId: Int, tag: String, ghToken: String) {
        val json = mapper.createObjectNode()
        json.put("tag_name", tag)
        json.put("body", "Delivery of '$tag'")
        val body = mapper.writeValueAsString(json)

        val req = Request.Builder()
            .from(getGitHubRepoReleasesByIdUri(repoId), ghAppProperties.name, ghToken)
            .post(body.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        httpClient.call(req)
    }

    fun deleteTagFromRepo(repoId: Int, tag: String, ghToken: String) {
        val req = Request.Builder()
            .from(getGitHubRefTagUri(repoId, tag), ghAppProperties.name, ghToken)
            .delete()
            .build()

        httpClient.call(req)
    }

    fun getTeam(orgId: Int, teamId: Int, installationToken: String): GitHubTeamResponse {
        val req = Request.Builder()
            .from(getGitHubTeamByIdUri(orgId, teamId), ghAppProperties.name, installationToken)
            .build()

        return httpClient.callAndMap(req, mapper, GitHubTeamResponse::class.java, TEAM_INFO_CACHE)
    }

    fun createTeam(orgId: Int, name: String, installationToken: String): GitHubTeamResponse {
        val json = mapper.createObjectNode()
        json.put("name", name)
        json.put("privacy", "secret")
        val body = mapper.writeValueAsString(json)

        val req = Request.Builder()
            .from(getGitHubTeamsUri(orgId), ghAppProperties.name, installationToken)
            .post(body.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        return httpClient.callAndMap(req, mapper, GitHubTeamResponse::class.java)
    }

    fun deleteTeam(orgId: Int, teamId: Int, installationToken: String) {
        val req = Request.Builder()
            .from(getGitHubTeamByIdUri(orgId, teamId), ghAppProperties.name, installationToken)
            .delete()
            .build()

        httpClient.call(req)
    }

    fun addUserToTeam(orgId: Int, teamId: Int, username: String, installationToken: String) {
        val req = Request.Builder()
            .from(getGitHubTeamUserMembershipUri(orgId, teamId, username), ghAppProperties.name, installationToken)
            .put(FormBody.Builder().build())
            .build()

        httpClient.call(req)
    }

    fun removeUserFromTeam(orgId: Int, teamId: Int, username: String, installationToken: String) {
        val req = Request.Builder()
            .from(getGitHubTeamUserMembershipUri(orgId, teamId, username), ghAppProperties.name, installationToken)
            .delete()
            .build()

        httpClient.call(req)
    }

    fun addTeamToRepo(orgId: Int, orgName: String, repoName: String, teamId: Int, installationToken: String) {
        val json = mapper.createObjectNode()
        json.put("permission", "push")
        val body = mapper.writeValueAsString(json)

        val req = Request.Builder()
            .from(getGitHubTeamRepoUri(orgId, teamId, orgName, repoName), ghAppProperties.name, installationToken)
            .put(body.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        httpClient.call(req)
    }

    fun inviteUserToOrg(orgId: Int, userId: Int, teamId: Int? = null, installationToken: String) {
        val json = mapper.createObjectNode()
        json.put("invitee_id", userId)
        if (teamId != null) {
            json.set<JsonNode>("team_ids", mapper.createArrayNode().add(teamId))
        }
        val body = mapper.writeValueAsString(json)

        val req = Request.Builder()
            .from(getGitHubOrgInvitationsUri(orgId), ghAppProperties.name, installationToken)
            .post(body.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        return httpClient.call(req)
    }
}