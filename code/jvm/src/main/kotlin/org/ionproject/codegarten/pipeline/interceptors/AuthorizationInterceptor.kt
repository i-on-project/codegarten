package org.ionproject.codegarten.pipeline.interceptors

import org.ionproject.codegarten.database.dao.UserDao
import org.ionproject.codegarten.database.helpers.AccessTokenDb
import org.ionproject.codegarten.database.helpers.UsersDb
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Target(AnnotationTarget.FUNCTION)
annotation class RequiresUserAuth

const val AUTH_SCHEME = "Token"
const val AUTH_HEADER = "Authorization"

const val USER_ATTRIBUTE = "user-attribute"

@Component
class AuthorizationInterceptor(val tokenDb: AccessTokenDb, val usersDb: UsersDb) : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(AuthorizationInterceptor::class.java)

    fun getAndVerifyUser(accessToken: String): UserDao? {
        val accessTokenDao = tokenDb.getAccessToken(accessToken)
        // TODO: May not exist
        if (accessTokenDao.expiration_date.isBefore(LocalDateTime.now())) {
            return null
        }

        return usersDb.getUserById(accessTokenDao.user_id)
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val routeHandler = handler as? HandlerMethod

        if (routeHandler?.hasMethodAnnotation(RequiresUserAuth::class.java) == true) {
            logger.info("AuthorizationInterceptor - PreHandle with handler ${handler.javaClass.name} requires authentication")
            val authorizationHeader = request.getHeader(AUTH_HEADER)?.trim()

            if (authorizationHeader != null && authorizationHeader.startsWith(AUTH_SCHEME, true)) {
                val accessToken = authorizationHeader.drop(AUTH_SCHEME.length + 1).trim()

                val userDao = getAndVerifyUser(accessToken)
                if (userDao != null) {
                    request.setAttribute(USER_ATTRIBUTE, userDao)
                    logger.info("AuthorizationInterceptor - Token from user '${userDao.name}' is valid")
                    return true
                } else {
                    logger.info("AuthorizationInterceptor - Token is invalid")
                    response.status = HttpServletResponse.SC_FORBIDDEN
                    return false
                }
            } else {
                logger.info("AuthorizationInterceptor - Authorization header was not provided")
                response.status = HttpServletResponse.SC_FORBIDDEN
                // TODO: Problem JSON
                return false
            }
        } else {
            return true
        }
    }
}