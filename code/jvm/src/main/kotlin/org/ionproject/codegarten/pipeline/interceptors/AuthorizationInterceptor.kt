package org.ionproject.codegarten.pipeline.interceptors

import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.auth.AuthHeaderValidator
import org.ionproject.codegarten.auth.AuthHeaderValidator.AUTH_HEADER
import org.ionproject.codegarten.database.dto.AccessToken
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.helpers.AccessTokensDb
import org.ionproject.codegarten.database.helpers.UsersDb
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.ionproject.codegarten.exceptions.NotFoundException
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.responses.GitHubUserOrgRole.NOT_A_MEMBER
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
annotation class RequiresUserAuth

@Target(AnnotationTarget.FUNCTION)
annotation class RequiresUserInOrg

const val USER_ATTRIBUTE = "user-attribute"

@Component
class AuthorizationInterceptor(
    val tokensDb: AccessTokensDb,
    val usersDb: UsersDb,
    val gitHubInterface: GitHubInterface,
    val cryptoUtils: CryptoUtils
) : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(AuthorizationInterceptor::class.java)

    fun getAndVerifyUser(token: String): User {
        val accessToken: AccessToken
        try {
            accessToken = tokensDb.getAccessToken(cryptoUtils.hash(token))
        } catch(e: NotFoundException) {
            throw AuthorizationException("Bad credentials")
        }

        if (accessToken.expiration_date.isBefore(OffsetDateTime.now())) {
            throw AuthorizationException("Access token has expired at ${accessToken.expiration_date}")
        }

        return usersDb.getUserById(accessToken.user_id)
    }

    fun verifyUserInOrg(orgId: Int, user: User): Boolean {
        val membership = gitHubInterface.getUserOrgMembership(
            orgId,
            user.gh_id,
            user.gh_token
        )

        if (membership.role == NOT_A_MEMBER) {
            throw AuthorizationException("User is not in the organization")
        }

        return true
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val routeHandler = handler as? HandlerMethod ?: return true

        val requiresUserAuth = routeHandler.hasMethodAnnotation(RequiresUserAuth::class.java)
        val requiresUserInOrg = routeHandler.hasMethodAnnotation(RequiresUserInOrg::class.java)

        if (requiresUserAuth || requiresUserInOrg) {
            // Handler has 'RequiresUserAuth' annotation. We need to decode the auth header
            val authorizationHeader = request.getHeader(AUTH_HEADER)?.trim()
            logger.info("PreHandle with handler ${handler.javaClass.name} requires authentication")

            val encryptedUser = AuthHeaderValidator.validate(authorizationHeader, ::getAndVerifyUser)

            val user = User(
                encryptedUser.uid,
                encryptedUser.name,
                encryptedUser.gh_id,
                cryptoUtils.decrypt(encryptedUser.gh_token)
            )

            // User is valid
            request.setAttribute(USER_ATTRIBUTE, user)
            logger.info("User with name '${user.name}' has a valid access_token")

            if (requiresUserInOrg) {
                // Get the path variables
                val pathVars = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE) as Map<String, String>

                return verifyUserInOrg(pathVars[ORG_PARAM]!!.toInt(), user)
            }
        }

        return true
    }
}