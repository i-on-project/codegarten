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

class GitHubInterfaceMock : GitHubInterface {
    override fun getAuthUri(state: String): URI {
        TODO("Not yet implemented")
    }

    override fun getInstallationUri(): URI {
        TODO("Not yet implemented")
    }

    override fun getAccessTokenFromAuthCode(authCode: String): GitHubUserAccessTokenResponse {
        TODO("Not yet implemented")
    }

    override fun getUserInfo(accessToken: String): GitHubLoginResponse {
        TODO("Not yet implemented")
    }

    override fun getInstallationOrg(installationId: Int): GitHubInstallationResponse {
        TODO("Not yet implemented")
    }

    override fun getOrgInstallation(orgId: Int): GitHubInstallationResponse {
        TODO("Not yet implemented")
    }

    override fun getInstallationToken(installationId: Int): GitHubInstallationAccessTokenResponse {
        TODO("Not yet implemented")
    }

    override fun getUser(userId: Int, accessToken: String): GitHubLoginResponse {
        TODO("Not yet implemented")
    }

    override fun getUserOrgMembership(orgId: Int, accessToken: String): GitHubOrgMembershipResponse {
        TODO("Not yet implemented")
    }

    override fun getUserOrgs(accessToken: String, page: Int, limit: Int): List<GitHubOrganizationResponse> {
        TODO("Not yet implemented")
    }

    override fun getOrgById(orgId: Int, accessToken: String): GitHubOrganizationResponse {
        TODO("Not yet implemented")
    }

    override fun getRepoById(repoId: Int, accessToken: String): GitHubRepoResponse {
        TODO("Not yet implemented")
    }

    override fun getRepoByName(login: String, repoName: String, accessToken: String): GitHubRepoResponse {
        TODO("Not yet implemented")
    }

    override fun getRepo(req: Request): GitHubRepoResponse {
        TODO("Not yet implemented")
    }

    override fun createRepo(orgId: Int, repoName: String, installationToken: String): GitHubRepoResponse {
        TODO("Not yet implemented")
    }

    override fun createRepoFromTemplate(
        orgName: String,
        repoName: String,
        repoTemplateId: Int,
        installationToken: String
    ): GitHubRepoResponse {
        TODO("Not yet implemented")
    }

    override fun deleteRepo(repoId: Int, installationToken: String) {
        TODO("Not yet implemented")
    }

    override fun searchRepos(orgName: String, toSearch: String?, ghToken: String): GitHubRepoSearchResponse {
        TODO("Not yet implemented")
    }

    override fun addUserToRepo(repoId: Int, username: String, installationToken: String) {
        TODO("Not yet implemented")
    }

    override fun getAllTagsFromRepo(repoId: Int, ghToken: String): List<GitHubTag> {
        TODO("Not yet implemented")
    }

    override fun tryGetTagFromRepo(repoId: Int, tag: String, ghToken: String): Optional<GitHubTag> {
        TODO("Not yet implemented")
    }

    override fun createReleaseInRepo(repoId: Int, tag: String, ghToken: String) {
        TODO("Not yet implemented")
    }

    override fun deleteTagFromRepo(repoId: Int, tag: String, ghToken: String) {
        TODO("Not yet implemented")
    }

    override fun getTeam(orgId: Int, teamId: Int, installationToken: String): GitHubTeamResponse {
        TODO("Not yet implemented")
    }

    override fun createTeam(orgId: Int, name: String, installationToken: String): GitHubTeamResponse {
        TODO("Not yet implemented")
    }

    override fun deleteTeam(orgId: Int, teamId: Int, installationToken: String) {
        TODO("Not yet implemented")
    }

    override fun addUserToTeam(orgId: Int, teamId: Int, username: String, installationToken: String) {
        TODO("Not yet implemented")
    }

    override fun removeUserFromTeam(orgId: Int, teamId: Int, username: String, installationToken: String) {
        TODO("Not yet implemented")
    }

    override fun addTeamToRepo(orgId: Int, orgName: String, repoName: String, teamId: Int, installationToken: String) {
        TODO("Not yet implemented")
    }

    override fun inviteUserToOrg(orgId: Int, userId: Int, teamId: Int?, installationToken: String) {
        TODO("Not yet implemented")
    }
}