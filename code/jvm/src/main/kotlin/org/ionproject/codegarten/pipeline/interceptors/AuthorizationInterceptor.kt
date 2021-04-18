package org.ionproject.codegarten.pipeline.interceptors

import org.ionproject.codegarten.database.dao.UserDao
import org.ionproject.codegarten.database.helpers.UsersDb
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.Base64Utils
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Target(AnnotationTarget.FUNCTION)
annotation class RequiresAuth

const val AUTH_SCHEME = "Token"
const val AUTH_HEADER = "Authorization"

const val USER_ATTRIBUTE = "user-attribute"

@Component
class AuthorizationInterceptor(val db: UsersDb) : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(AuthorizationInterceptor::class.java)

    fun getAndVerifyUser(username: String, password: String): UserDao? {
        TODO()
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val routeHandler = handler as? HandlerMethod

        if (routeHandler?.hasMethodAnnotation(RequiresAuth::class.java) == true) {
            // username:pwd
            logger.info("AuthorizationInterceptor - PreHandle with handler ${handler.javaClass.name} requires authentication")
            val authorizationHeader = request.getHeader(AUTH_HEADER)?.trim()

            if (authorizationHeader != null && authorizationHeader.startsWith(AUTH_SCHEME, true)) {
                val credentials = authorizationHeader.drop(AUTH_SCHEME.length + 1).trim()
                val (username, password) = String(Base64Utils.decodeFromString(credentials)).split(':')

                val userDao = getAndVerifyUser(username, password)
                if (userDao != null) {
                    request.setAttribute(USER_ATTRIBUTE, userDao)
                    logger.info("AuthorizationInterceptor - User with name ${username} is valid")
                    return true
                } else {
                    logger.info("AuthorizationInterceptor - User with name ${username} is invalid")
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