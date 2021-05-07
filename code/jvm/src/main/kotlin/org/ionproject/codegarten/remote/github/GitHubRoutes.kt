package org.ionproject.codegarten.remote.github

import org.ionproject.codegarten.Routes.LIMIT_PARAM
import org.ionproject.codegarten.Routes.PAGE_PARAM
import org.springframework.web.util.UriTemplate

object GitHubRoutes {

    // This accept value allows checking if a repository is a template via the is_template key
    val ACCEPT_CONTENT_TYPE = "application/vnd.github.baptiste-preview+json"

    const val GITHUB_HOST = "https://github.com"
    const val GITHUB_AVATAR_HOST = "https://avatars.githubusercontent.com"
    const val GITHUB_API_HOST = "https://api.github.com"

    const val APP_PARAM = "appName"
    const val INSTALLATION_PARAM = "installationId"
    const val CLIENT_ID_PARAM = "clientId"
    const val STATE_PARAM = "state"
    const val USER_PARAM = "userId"
    const val ORG_PARAM = "orgId"
    const val LOGIN_PARAM = "login"
    const val REPO_ID_PARAM = "repoId"
    const val REPO_NAME_PARAM = "repoName"
    const val TAG_PARAM = "tag"
    const val TEAM_PARAM = "teamId"


    // OAuth
    const val GITHUB_AUTH_BASE = "$GITHUB_HOST/login/oauth"
    const val GITHUB_AUTH_URI = "$GITHUB_AUTH_BASE/authorize?client_id={$CLIENT_ID_PARAM}&state={$STATE_PARAM}&response_type=code"
    const val GITHUB_TOKEN_URI = "$GITHUB_AUTH_BASE/access_token"

    val GITHUB_AUTH_URI_TEMPLATE = UriTemplate(GITHUB_AUTH_URI)

    fun getGitHubAuthUri(clientId: String, state: String) = GITHUB_AUTH_URI_TEMPLATE.expand(clientId, state)


    // HTML
    const val GITHUB_LOGIN_URI = "$GITHUB_HOST/{$LOGIN_PARAM}"

    val GITHUB_LOGIN_URI_TEMPLATE = UriTemplate(GITHUB_LOGIN_URI)

    fun getGithubLoginUri(login: String) = GITHUB_LOGIN_URI_TEMPLATE.expand(login)


    // User
    const val GITHUB_USER_URI = "$GITHUB_API_HOST/user"
    const val GITHUB_USER_BY_ID_URI = "$GITHUB_USER_URI/{$USER_PARAM}"
    const val GITHUB_USER_AVATAR_URI = "$GITHUB_AVATAR_HOST/u/{$USER_PARAM}"
    const val GITHUB_TEAM_AVATAR_URI = "$GITHUB_AVATAR_HOST/t/{$USER_PARAM}"

    val GITHUB_USER_BY_ID_URI_TEMPLATE = UriTemplate(GITHUB_USER_BY_ID_URI)
    val GITHUB_USER_AVATAR_URI_TEMPLATE = UriTemplate(GITHUB_USER_AVATAR_URI)
    val GITHUB_TEAM_AVATAR_URI_TEMPLATE = UriTemplate(GITHUB_TEAM_AVATAR_URI)

    fun getGitHubUserByIdUri(userId: Int) = GITHUB_USER_BY_ID_URI_TEMPLATE.expand(userId)
    fun getGitHubUserAvatarUri(userId: Int) = GITHUB_USER_AVATAR_URI_TEMPLATE.expand(userId)
    fun getGitHubTeamAvatarUri(teamId: Int) = GITHUB_TEAM_AVATAR_URI_TEMPLATE.expand(teamId)


    // User Organizations
    const val GITHUB_USER_ORGS_URI = "$GITHUB_USER_URI/orgs?page={$PAGE_PARAM}&per_page={$LIMIT_PARAM}"

    val GITHUB_USER_ORGS_URI_TEMPLATE = UriTemplate(GITHUB_USER_ORGS_URI)

    fun getGithubUserOrgsUri(page: Int, limit: Int) = GITHUB_USER_ORGS_URI_TEMPLATE.expand(page + 1, limit)

    // Organizations
    const val GITHUB_ORGS_URI = "$GITHUB_API_HOST/organizations"
    const val GITHUB_ORG_URI = "$GITHUB_ORGS_URI/{$ORG_PARAM}"
    const val GITHUB_ORG_MEMBERSHIPS_URI = "$GITHUB_USER_URI/memberships/organizations"
    const val GITHUB_ORG_MEMBERSHIP_URI = "$GITHUB_ORG_MEMBERSHIPS_URI/{$ORG_PARAM}"

    val GITHUB_ORG_URI_TEMPLATE = UriTemplate(GITHUB_ORG_URI)
    val GITHUB_ORG_MEMBERSHIP_TEMPLATE = UriTemplate(GITHUB_ORG_MEMBERSHIP_URI)

    fun getGitHubOrgUri(orgId: Int) = GITHUB_ORG_URI_TEMPLATE.expand(orgId)
    fun getGitHubMembershipUri(orgId: Int) = GITHUB_ORG_MEMBERSHIP_TEMPLATE.expand(orgId)


    // Installations
    const val GITHUB_NEW_INSTALLATION_URI = "$GITHUB_HOST/apps/{$APP_PARAM}/installations/new"
    const val GITHUB_INSTALLATIONS_URI = "$GITHUB_API_HOST/app/installations"
    const val GITHUB_INSTALLATION_URI = "$GITHUB_INSTALLATIONS_URI/{$INSTALLATION_PARAM}"
    const val GITHUB_INSTALLATION_ACCESS_TOKEN_URI = "$GITHUB_INSTALLATION_URI/access_tokens"
    const val GITHUB_INSTALLATION_OF_ORG_URI = "$GITHUB_ORG_URI/installation"

    val GITHUB_NEW_INSTALLATION_URI_TEMPLATE = UriTemplate(GITHUB_NEW_INSTALLATION_URI)
    val GITHUB_INSTALLATION_URI_TEMPLATE = UriTemplate(GITHUB_INSTALLATION_URI)
    val GITHUB_INSTALLATION_ACCESS_TOKEN_URI_TEMPLATE = UriTemplate(GITHUB_INSTALLATION_ACCESS_TOKEN_URI)
    val GITHUB_INSTALLATION_OF_ORG_URI_TEMPLATE = UriTemplate(GITHUB_INSTALLATION_OF_ORG_URI)

    fun getGitHubNewInstallationUri(appName: String) = GITHUB_NEW_INSTALLATION_URI_TEMPLATE.expand(appName)
    fun getGitHubInstallationUri(installationId: Int) = GITHUB_INSTALLATION_URI_TEMPLATE.expand(installationId)
    fun getGitHubInstallationAccessTokenUri(installationId: Int) =
        GITHUB_INSTALLATION_ACCESS_TOKEN_URI_TEMPLATE.expand(installationId)
    fun getGitHubInstallationOfOrgUri(orgId: Int) = GITHUB_INSTALLATION_OF_ORG_URI_TEMPLATE.expand(orgId)


    // Repositories
    const val GITHUB_REPOS_ID_URI = "$GITHUB_API_HOST/repositories"
    const val GITHUB_REPO_ID_URI = "$GITHUB_REPOS_ID_URI/{$REPO_ID_PARAM}"
    const val GITHUB_REPO_ID_GENERATE_URI = "$GITHUB_REPO_ID_URI/generate"
    const val GITHUB_REPO_COLLABORATORS_URI = "$GITHUB_REPO_ID_URI/collaborators"
    const val GITHUB_REPO_COLLABORATOR_URI = "$GITHUB_REPO_COLLABORATORS_URI/{$LOGIN_PARAM}"
    const val GITHUB_REPOS_NAME_URI = "$GITHUB_API_HOST/repos"
    const val GITHUB_REPO_NAME_URI = "$GITHUB_REPOS_NAME_URI/{$LOGIN_PARAM}/{$REPO_ID_PARAM}"
    const val GITHUB_REPOS_OF_ORG_URI = "$GITHUB_ORG_URI/repos"


    val GITHUB_REPO_ID_URI_TEMPLATE = UriTemplate(GITHUB_REPO_ID_URI)
    val GITHUB_REPO_ID_GENERATE_URI_TEMPLATE = UriTemplate(GITHUB_REPO_ID_GENERATE_URI)
    val GITHUB_REPO_NAME_URI_TEMPLATE = UriTemplate(GITHUB_REPO_NAME_URI)
    val GITHUB_REPOS_OF_ORG_URI_TEMPLATE = UriTemplate(GITHUB_REPOS_OF_ORG_URI)
    val GITHUB_REPO_COLLABORATOR_URI_TEMPLATE = UriTemplate(GITHUB_REPO_COLLABORATOR_URI)

    fun getGitHubRepoByIdUri(repoId: Int) = GITHUB_REPO_ID_URI_TEMPLATE.expand(repoId)
    fun getGitHubRepoGenerateByIdUri(repoId: Int) = GITHUB_REPO_ID_GENERATE_URI_TEMPLATE.expand(repoId)
    fun getGitHubRepoByNameUri(login: String, repoName: String) = GITHUB_REPO_NAME_URI_TEMPLATE.expand(login, repoName)
    fun getGitHubReposOfOrgUri(orgId: Int) = GITHUB_REPOS_OF_ORG_URI_TEMPLATE.expand(orgId)
    fun getGitHubRepoCollaboratorUri(repoId: Int, login: String) = GITHUB_REPO_COLLABORATOR_URI_TEMPLATE.expand(repoId, login)


    // Refs/Tags
    const val GITHUB_REF_TAG_PREFIX = "refs/tags/"
    const val GITHUB_REFS_TAGS_URI = "$GITHUB_REPO_ID_URI/git/refs/tags"
    const val GITHUB_REF_TAG_URI = "$GITHUB_REPO_ID_URI/git/refs/tags/{$TAG_PARAM}"

    val GITHUB_REFS_TAGS_URI_TEMPLATE = UriTemplate(GITHUB_REFS_TAGS_URI)
    val GITHUB_REF_TAG_URI_TEMPLATE = UriTemplate(GITHUB_REF_TAG_URI)

    fun getGitHubRefsTagsUri(repoId: Int) = GITHUB_REFS_TAGS_URI_TEMPLATE.expand(repoId)
    fun getGitHubRefTagUri(repoId: Int, tag: String) = GITHUB_REF_TAG_URI_TEMPLATE.expand(repoId, tag)

    // Teams
    const val GITHUB_TEAMS_URI = "$GITHUB_ORG_URI/teams"
    const val GITHUB_TEAMS_ID_URI = "$GITHUB_ORG_URI/team"
    const val GITHUB_TEAM_ID_URI = "$GITHUB_TEAMS_ID_URI/{$TEAM_PARAM}"
    const val GITHUB_TEAM_MEMBERSHIPS_URI = "$GITHUB_TEAM_ID_URI/memberships"
    const val GITHUB_TEAM_USER_MEMBERSHIP_URI = "$GITHUB_TEAM_MEMBERSHIPS_URI/{$LOGIN_PARAM}"
    const val GITHUB_TEAM_REPOS_URI = "$GITHUB_TEAM_ID_URI/repos"
    const val GITHUB_TEAM_REPO_URI = "$GITHUB_TEAM_REPOS_URI/{$LOGIN_PARAM}/{$REPO_NAME_PARAM}"

    val GITHUB_TEAMS_URI_TEMPLATE = UriTemplate(GITHUB_TEAMS_URI)
    val GITHUB_TEAMS_ID_URI_TEMPLATE = UriTemplate(GITHUB_TEAMS_ID_URI)
    val GITHUB_TEAM_ID_URI_TEMPLATE = UriTemplate(GITHUB_TEAM_ID_URI)
    val GITHUB_TEAM_MEMBERSHIPS_URI_TEMPLATE = UriTemplate(GITHUB_TEAM_MEMBERSHIPS_URI)
    val GITHUB_TEAM_USER_MEMBERSHIP_URI_TEMPLATE = UriTemplate(GITHUB_TEAM_USER_MEMBERSHIP_URI)
    val GITHUB_TEAM_REPOS_URI_TEMPLATE = UriTemplate(GITHUB_TEAM_REPOS_URI)
    val GITHUB_TEAM_REPO_URI_TEMPLATE = UriTemplate(GITHUB_TEAM_REPO_URI)

    fun getGitHubTeamsUri(orgId: Int) = GITHUB_TEAMS_URI_TEMPLATE.expand(orgId)
    fun getGitHubTeamsByIdUri(orgId: Int) = GITHUB_TEAMS_ID_URI_TEMPLATE.expand(orgId)
    fun getGitHubTeamByIdUri(orgId: Int, teamId: Int) = GITHUB_TEAM_ID_URI_TEMPLATE.expand(orgId, teamId)
    fun getGitHubTeamMembershipUri(orgId: Int, teamId: Int) = GITHUB_TEAM_MEMBERSHIPS_URI_TEMPLATE.expand(orgId, teamId)
    fun getGitHubTeamUserMembershipUri(orgId: Int, teamId: Int, login: String) = GITHUB_TEAM_USER_MEMBERSHIP_URI_TEMPLATE.expand(orgId, teamId, login)
    fun getGitHubTeamReposUri(orgId: Int, teamId: Int) = GITHUB_TEAM_REPOS_URI_TEMPLATE.expand(orgId, teamId)
    fun getGitHubTeamRepoUri(orgId: Int, teamId: Int, orgName: String, repoName: String) =
        GITHUB_TEAM_REPO_URI_TEMPLATE.expand(orgId, teamId, orgName, repoName)

    // Aux functions
    fun getGitHubTagNameFromRef(ref: String) = ref.replaceFirst(GITHUB_REF_TAG_PREFIX, "")

    fun generateCodeGartenRepoName(classroomNumber: Int, prefix: String, username: String) =
        "CG$classroomNumber-$prefix-$username"

    fun generateCodeGartenTeamName(classroomNumber: Int, teamName: String) =
        "CG$classroomNumber-$teamName"
}