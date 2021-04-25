package org.ionproject.codegarten.auth

import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.slf4j.LoggerFactory
import org.springframework.util.Base64Utils

object AuthHeaderValidator {
    const val AUTH_SCHEME = "Bearer"
    const val AUTH_HEADER = "Authorization"

    private val logger = LoggerFactory.getLogger(AuthHeaderValidator::class.java)

    fun validate(header: String?, validateUserFunction: (token: String) -> User): User {
        if (header == null) {
            logger.info("Authorization header was not provided")
            throw AuthorizationException("Resource requires authentication")
        }
        if (!header.startsWith(AUTH_SCHEME, true)) {
            logger.info("Authorization header didn't follow the auth basic scheme")
            throw AuthorizationException("Invalid authorization scheme")
        }

        // Get user credentials
        val token: String
        try {
            val credentials = header.drop(AUTH_SCHEME.length + 1).trim()
            token = String(Base64Utils.decodeFromString(credentials))
        } catch(ex: Exception) {
            logger.info("Could not get user credentials")
            throw AuthorizationException("Bad credentials")
        }

        return validateUserFunction(token)
    }
}