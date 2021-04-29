package org.ionproject.codegarten.pipeline.interceptors

import org.ionproject.codegarten.auth.AuthHeaderValidator
import org.ionproject.codegarten.auth.AuthHeaderValidator.AUTH_HEADER
import org.ionproject.codegarten.database.dto.AccessToken
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.helpers.AccessTokensDb
import org.ionproject.codegarten.database.helpers.UsersDb
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.ionproject.codegarten.exceptions.NotFoundException
import org.ionproject.codegarten.remote.GitHubInterface
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
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
) : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(AuthorizationInterceptor::class.java)

    fun getAndVerifyUser(token: String): User {
        val accessToken: AccessToken
        try {
            accessToken = tokensDb.getAccessToken(token)
        } catch(e: NotFoundException) {
            throw AuthorizationException("Bad credentials")
        }

        if (accessToken.expiration_date.isBefore(OffsetDateTime.now())) {
            throw AuthorizationException("Access token has expired at ${accessToken.expiration_date}")
        }

        return usersDb.getUserById(accessToken.user_id)
    }

    fun verifyUserInOrg() {
        //TODO
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val routeHandler = handler as? HandlerMethod

        if (routeHandler == null ||
            !routeHandler.hasMethodAnnotation(RequiresUserAuth::class.java) &&
            !routeHandler.hasMethodAnnotation(RequiresUserInOrg::class.java)
        ) {
            return true
        }

        // Handler has 'RequiresUserAuth' annotation. We need to decode the auth header
        val authorizationHeader = request.getHeader(AUTH_HEADER)?.trim()
        logger.info("PreHandle with handler ${handler.javaClass.name} requires authentication")

        val user = AuthHeaderValidator.validate(authorizationHeader, ::getAndVerifyUser)

        // User is valid
        request.setAttribute(USER_ATTRIBUTE, user)
        logger.info("User with name '${user.name}' has a valid access_token")

        if (routeHandler.hasMethodAnnotation(RequiresUserInOrg::class.java)) {
            // TODO
        }

        return true
    }
}