package org.ionproject.codegarten.pipeline.interceptors

import org.ionproject.codegarten.Routes.INVITE_CODE_PARAM
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.database.dto.Installation
import org.ionproject.codegarten.database.helpers.InstallationsDb
import org.ionproject.codegarten.database.helpers.InviteCodesDb
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.exceptions.NotFoundException
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.responses.GitHubInstallationAccessTokenResponse
import org.ionproject.codegarten.remote.github.responses.GitHubInstallationResponse
import org.ionproject.codegarten.utils.CryptoUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.HandlerMapping
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Target(AnnotationTarget.FUNCTION)
annotation class RequiresGhAppInstallation

const val INSTALLATION_ATTRIBUTE = "installation-attribute"
const val INVITE_CODE_ATTRIBUTE = "invite-code-attribute"

private const val INSTALLATION_TOKEN_THRESHOLD_MINUTES = 1L

@Component
class InstallationInterceptor(
    val installationsDb: InstallationsDb,
    val inviteCodesDb: InviteCodesDb,
    val gitHub: GitHubInterface,
    val cryptoUtils: CryptoUtils,
) : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(InstallationInterceptor::class.java)

    private fun getNewInstallationToken(orgId: Int): Installation {
        val installation: GitHubInstallationResponse
        val tokenResponse: GitHubInstallationAccessTokenResponse
        try {
            installation = gitHub.getOrgInstallation(orgId)
            tokenResponse = gitHub.getInstallationToken(installation.id)
        } catch (ex: NotFoundException) {
            throw NotFoundException("GitHub App is not installed in the organization")
        }

        val encryptedToken = cryptoUtils.encrypt(tokenResponse.token)

        installationsDb.createOrUpdateInstallation(
            installationId = installation.id,
            orgId = orgId,
            accessToken = encryptedToken,
            expirationDate = tokenResponse.expires_at
        )

        return Installation(installation.id, orgId, encryptedToken, tokenResponse.expires_at)
    }

    private fun updateInstallationToken(orgId: Int, installation: Installation): Installation {
        try {
            val tokenResponse = gitHub.getInstallationToken(installation.iid)
            val encryptedToken = cryptoUtils.encrypt(tokenResponse.token)

            installationsDb.editInstallation(
                installationId = installation.iid,
                orgId = installation.org_id,
                accessToken = encryptedToken,
                expirationDate = tokenResponse.expires_at
            )

            return Installation(installation.iid, installation.org_id, encryptedToken, tokenResponse.expires_at)
        } catch (ex: NotFoundException) {
            // This happens when the installation token is invalid

            return getNewInstallationToken(orgId)
        }
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val routeHandler = handler as? HandlerMethod ?: return true

        val requiresInstallation = routeHandler.hasMethodAnnotation(RequiresGhAppInstallation::class.java)
        if (!requiresInstallation) return true

        logger.info("PreHandle with handler ${handler.javaClass.name} requires GitHub App Installation")

        val pathVars = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE) as Map<String, String>
        val invCodePath = pathVars[INVITE_CODE_PARAM]

        val orgId =
            if (invCodePath != null) {
                // Check if invite code was provided
                val invCode = inviteCodesDb.getInviteCode(invCodePath)
                request.setAttribute(INVITE_CODE_ATTRIBUTE, invCode)
                invCode.org_id
            } else {
                pathVars[ORG_PARAM]?.toIntOrNull() ?: throw InvalidInputException("Invalid organization id")
            }

        var installation = installationsDb
            .tryGetInstallationByOrgId(orgId)
            .orElseGet {
                // If the installation is not present in the database, we need to insert it
                getNewInstallationToken(orgId)
            }

        val isExpired = installation.expiration_date.isBefore(
            OffsetDateTime.now().plusMinutes(INSTALLATION_TOKEN_THRESHOLD_MINUTES)
        )

        if (isExpired) {
            // If the token is expired, we need to update it
            installation = updateInstallationToken(orgId, installation)
        }


        request.setAttribute(
            INSTALLATION_ATTRIBUTE,
            Installation(
                installation.iid,
                installation.org_id,
                cryptoUtils.decrypt(installation.accessToken),
                installation.expiration_date)
        )

        return true
    }
}