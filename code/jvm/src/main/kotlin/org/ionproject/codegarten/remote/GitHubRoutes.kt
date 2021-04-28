package org.ionproject.codegarten.remote

import org.springframework.http.MediaType
import org.springframework.web.util.UriTemplate

object GitHubRoutes {

    val ACCEPT_CONTENT_TYPE = MediaType.APPLICATION_JSON_VALUE

    const val GITHUB_HOST = "https://github.com"
    const val GITHUB_API_HOST = "https://api.github.com"

    const val APP_PARAM = "appName"
    const val INSTALLATION_PARAM = "installationId"
    const val CLIENT_ID_PARAM = "clientId"
    const val STATE_PARAM = "state"



    // OAuth
    const val GITHUB_AUTH_BASE = "$GITHUB_HOST/login/oauth"
    const val GITHUB_AUTH_URI = "$GITHUB_AUTH_BASE/authorize?client_id={$CLIENT_ID_PARAM}&state={$STATE_PARAM}&response_type=code"
    const val GITHUB_TOKEN_URI = "$GITHUB_AUTH_BASE/access_token"

    val GITHUB_AUTH_URI_TEMPLATE = UriTemplate(GITHUB_AUTH_URI)

    fun getGitHubAuthUri(clientId: String, state: String) = GITHUB_AUTH_URI_TEMPLATE.expand(clientId, state)


    // Installations
    const val GITHUB_NEW_INSTALLATION_URI = "$GITHUB_HOST/apps/{$APP_PARAM}/installations/new"
    const val GITHUB_INSTALLATIONS_URI = "$GITHUB_API_HOST/app/installations"
    const val GITHUB_INSTALLATION_URI = "$GITHUB_INSTALLATIONS_URI/{$INSTALLATION_PARAM}"
    const val GITHUB_INSTALLATION_ACCESS_TOKEN_URI = "$GITHUB_INSTALLATION_URI/access_tokens"

    val GITHUB_NEW_INSTALLATION_URI_TEMPLATE = UriTemplate(GITHUB_NEW_INSTALLATION_URI)
    val GITHUB_INSTALLATION_URI_TEMPLATE = UriTemplate(GITHUB_INSTALLATION_URI)
    val GITHUB_INSTALLATION_ACCESS_TOKEN_URI_TEMPLATE = UriTemplate(GITHUB_INSTALLATION_ACCESS_TOKEN_URI)

    fun getGitHubNewInstallationUri(appName: String) = GITHUB_NEW_INSTALLATION_URI_TEMPLATE.expand(appName)
    fun getGitHubInstallationUri(installationId: Int) = GITHUB_INSTALLATION_URI_TEMPLATE.expand(installationId)
    fun getGitHubInstallationAccessTokenUri(installationId: Int) =
        GITHUB_INSTALLATION_ACCESS_TOKEN_URI_TEMPLATE.expand(installationId)

    // User
    const val GITHUB_USER_URI = "$GITHUB_API_HOST/user"
}