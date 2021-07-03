package org.ionproject.codegarten.remote.github

import org.ionproject.codegarten.exceptions.NotFoundException
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
import org.ionproject.codegarten.remote.github.responses.GitHubUserOrgRole
import java.net.URI
import java.time.OffsetDateTime
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
        return GitHubLoginResponse(
            1234,
            "gitHubUsername",
            "www.example.com"
        )
    }

    override fun getInstallationOrg(installationId: Int): GitHubInstallationResponse {
        TODO("Not yet implemented")
    }

    override fun getOrgInstallation(orgId: Int): GitHubInstallationResponse {
        TODO("Not yet implemented")
    }

    override fun getInstallationToken(installationId: Int): GitHubInstallationAccessTokenResponse {
        return GitHubInstallationAccessTokenResponse("installationToken", OffsetDateTime.now().plusYears(50))
    }

    override fun getUser(userId: Int, accessToken: String): GitHubLoginResponse {
        return GitHubLoginResponse(
            1234,
            "gitHubUsername",
            "www.example.com"
        )
    }

    override fun getUserOrgMembership(orgId: Int, accessToken: String): GitHubOrgMembershipResponse {
        val membership =
            when(accessToken) {
                "gh_tokenAdmin" -> GitHubUserOrgRole.ADMIN
                "gh_tokenMember" -> GitHubUserOrgRole.MEMBER
                "gh_tokenNotMember" -> GitHubUserOrgRole.NOT_A_MEMBER
                else -> GitHubUserOrgRole.NOT_A_MEMBER
            }

        return GitHubOrgMembershipResponse(
            membership,
            GitHubLoginResponse(
                1234,
                "gitHubUsername",
                "www.example.com"
            )
        )
    }

    override fun getUserOrgs(accessToken: String, page: Int, limit: Int): List<GitHubOrganizationResponse> {
        return listOf(
            GitHubOrganizationResponse(1, "org1", "desc", "www.example.com"),
            GitHubOrganizationResponse(2, "org2", "desc", "www.example.com")
        )
    }

    override fun getOrgById(orgId: Int, accessToken: String): GitHubOrganizationResponse {
        if (orgId < 1 || orgId > 2) throw NotFoundException("GitHub Organization not found")
        return GitHubOrganizationResponse(orgId, "org${orgId}", "desc", "www.example.com")
    }

    override fun getRepoById(repoId: Int, accessToken: String): GitHubRepoResponse {
        return GitHubRepoResponse(
            repoId,
            "repo${repoId}",
            "desc",
            true,
            "www.example.com",
            false,
            GitHubLoginResponse(
                5678,
                "repoOwner",
                "www.example.com"
            )
        )
    }

    override fun getRepoByName(login: String, repoName: String, accessToken: String): GitHubRepoResponse {
        TODO("Not yet implemented")
    }

    override fun createRepo(orgId: Int, repoName: String, installationToken: String): GitHubRepoResponse {
        return GitHubRepoResponse(
            894746,
            "repo894746",
            "desc",
            true,
            "www.example.com",
            false,
            GitHubLoginResponse(
                5678,
                "repoOwner",
                "www.example.com"
            )
        )
    }

    override fun createRepoFromTemplate(
        orgName: String,
        repoName: String,
        repoTemplateId: Int,
        installationToken: String
    ): GitHubRepoResponse {
        return GitHubRepoResponse(
            252552531,
            "repo252552531",
            "desc",
            true,
            "www.example.com",
            false,
            GitHubLoginResponse(
                5678,
                "repoOwner",
                "www.example.com"
            )
        )
    }

    override fun deleteRepo(repoId: Int, installationToken: String) { }

    override fun searchRepos(orgName: String, toSearch: String?, ghToken: String): GitHubRepoSearchResponse {
        val repos = listOf(
            GitHubRepoResponse(
                1,
                "repo1",
                "desc",
                false,
                "www.example.com",
                true,
                GitHubLoginResponse(
                    1234,
                    "gitHubUsername",
                    "www.example.com"
                )
            ),
            GitHubRepoResponse(
                2,
                "repo2",
                "desc",
                true,
                "www.example.com",
                true,
                GitHubLoginResponse(
                    1234,
                    "gitHubUsername",
                    "www.example.com"
                )
            )
        ).filter {
            it.name.contains(toSearch!!)
        }

        return GitHubRepoSearchResponse(repos.size, repos)
    }

    override fun addUserToRepo(repoId: Int, username: String, installationToken: String) { }

    override fun getAllTagsFromRepo(repoId: Int, ghToken: String): List<GitHubTag> {
        return listOf(GitHubTag("Delivery1A1", OffsetDateTime.now()))
    }

    override fun tryGetTagFromRepo(repoId: Int, tag: String, ghToken: String): Optional<GitHubTag> {
        TODO("Not yet implemented")
    }

    override fun createReleaseInRepo(repoId: Int, tag: String, ghToken: String) { }

    override fun deleteTagFromRepo(repoId: Int, tag: String, ghToken: String) { }

    override fun getTeam(orgId: Int, teamId: Int, installationToken: String): GitHubTeamResponse {
        TODO("Not yet implemented")
    }

    override fun createTeam(orgId: Int, name: String, installationToken: String): GitHubTeamResponse {
        TODO("Not yet implemented")
    }

    override fun deleteTeam(orgId: Int, teamId: Int, installationToken: String) { }

    override fun addUserToTeam(orgId: Int, teamId: Int, username: String, installationToken: String) { }

    override fun removeUserFromTeam(orgId: Int, teamId: Int, username: String, installationToken: String) { }

    override fun addTeamToRepo(orgId: Int, orgName: String, repoName: String, teamId: Int, installationToken: String) { }

    override fun inviteUserToOrg(orgId: Int, userId: Int, teamId: Int?, installationToken: String) { }
}