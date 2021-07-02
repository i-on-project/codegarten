package org.ionproject.codegarten.remote.github

import okhttp3.Request
import org.ionproject.codegarten.remote.github.responses.GitHubInstallationAccessTokenResponse
import org.ionproject.codegarten.remote.github.responses.GitHubInstallationResponse
import org.ionproject.codegarten.remote.github.responses.GitHubLoginResponse
import org.ionproject.codegarten.remote.github.responses.GitHubOrgMembershipResponse
import org.ionproject.codegarten.remote.github.responses.GitHubOrganizationResponse
import org.ionproject.codegarten.remote.github.responses.GitHubRepoResponse
import org.ionproject.codegarten.remote.github.responses.GitHubRepoSearchResponse
import org.ionproject.codegarten.remote.github.responses.GitHubTag
import org.ionproject.codegarten.remote.github.responses.GitHubTeamResponse
import org.ionproject.codegarten.remote.github.responses.GitHubUserAccessTokenResponse
import java.net.URI
import java.util.*

interface GitHubInterface {
    fun getAuthUri(state: String): URI

    fun getInstallationUri(): URI

    fun getAccessTokenFromAuthCode(authCode: String): GitHubUserAccessTokenResponse

    fun getUserInfo(accessToken: String): GitHubLoginResponse

    fun getInstallationOrg(installationId: Int): GitHubInstallationResponse

    fun getOrgInstallation(orgId: Int): GitHubInstallationResponse

    fun getInstallationToken(installationId: Int): GitHubInstallationAccessTokenResponse

    fun getUser(userId: Int, accessToken: String): GitHubLoginResponse

    fun getUserOrgMembership(orgId: Int, accessToken: String): GitHubOrgMembershipResponse

    fun getUserOrgs(accessToken: String, page: Int, limit: Int): List<GitHubOrganizationResponse>

    fun getOrgById(orgId: Int, accessToken: String): GitHubOrganizationResponse

    fun getRepoById(repoId: Int, accessToken: String): GitHubRepoResponse

    fun getRepoByName(login: String, repoName: String, accessToken: String): GitHubRepoResponse

    fun getRepo(req: Request): GitHubRepoResponse

    fun createRepo(orgId: Int, repoName: String, installationToken: String): GitHubRepoResponse

    fun createRepoFromTemplate(orgName: String, repoName: String, repoTemplateId: Int, installationToken: String): GitHubRepoResponse

    fun deleteRepo(repoId: Int, installationToken: String)

    fun searchRepos(orgName: String, toSearch: String?, ghToken: String): GitHubRepoSearchResponse

    fun addUserToRepo(repoId: Int, username: String, installationToken: String)

    fun getAllTagsFromRepo(repoId: Int, ghToken: String): List<GitHubTag>

    fun tryGetTagFromRepo(repoId: Int, tag: String, ghToken: String): Optional<GitHubTag>

    fun createReleaseInRepo(repoId: Int, tag: String, ghToken: String)

    fun deleteTagFromRepo(repoId: Int, tag: String, ghToken: String)

    fun getTeam(orgId: Int, teamId: Int, installationToken: String): GitHubTeamResponse

    fun createTeam(orgId: Int, name: String, installationToken: String): GitHubTeamResponse

    fun deleteTeam(orgId: Int, teamId: Int, installationToken: String)

    fun addUserToTeam(orgId: Int, teamId: Int, username: String, installationToken: String)

    fun removeUserFromTeam(orgId: Int, teamId: Int, username: String, installationToken: String)

    fun addTeamToRepo(orgId: Int, orgName: String, repoName: String, teamId: Int, installationToken: String)

    fun inviteUserToOrg(orgId: Int, userId: Int, teamId: Int? = null, installationToken: String)
}