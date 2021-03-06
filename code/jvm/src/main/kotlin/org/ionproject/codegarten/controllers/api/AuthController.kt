package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.AUTH_REVOKE_HREF
import org.ionproject.codegarten.Routes.AUTH_TOKEN_HREF
import org.ionproject.codegarten.controllers.models.AuthorizationInputModel
import org.ionproject.codegarten.controllers.models.RevocationInputModel
import org.ionproject.codegarten.database.PsqlErrorCode
import org.ionproject.codegarten.database.dto.AuthCode
import org.ionproject.codegarten.database.getPsqlErrorCode
import org.ionproject.codegarten.database.helpers.AccessTokensDb
import org.ionproject.codegarten.database.helpers.AuthCodesDb
import org.ionproject.codegarten.database.helpers.ClientsDb
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.ionproject.codegarten.exceptions.ClientException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.exceptions.NotFoundException
import org.ionproject.codegarten.exceptions.ServerErrorException
import org.ionproject.codegarten.responses.AccessToken
import org.ionproject.codegarten.responses.Response
import org.ionproject.codegarten.responses.toResponseEntity
import org.ionproject.codegarten.utils.CodeWrapper
import org.ionproject.codegarten.utils.CryptoUtils
import org.jdbi.v3.core.JdbiException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

private const val NUMBER_OF_RETRIES = 10 // Used when generating unique access tokens

@RestController
class AuthController(
    val codesDb: AuthCodesDb,
    val accessTokensDb: AccessTokensDb,
    val clientsDb: ClientsDb,
    val cryptoUtils: CryptoUtils
) {

    @PostMapping(AUTH_TOKEN_HREF)
    fun getAccessToken(
        input: AuthorizationInputModel?
    ) : ResponseEntity<Response> {
        if (input == null) throw InvalidInputException("Missing body")
        if (input.client_id == null) throw InvalidInputException("Missing client_id")
        if (input.client_secret == null) throw InvalidInputException("Missing client_secret")
        if (input.code == null) throw InvalidInputException("Missing code")

        val client = clientsDb.getClientById(input.client_id)
        if (!cryptoUtils.validateHash(input.client_secret, client.secret)) {
            throw ClientException("Invalid client")
        }

        val authCode: AuthCode
        try {
            authCode = codesDb.getAuthCode(input.code)
            if (authCode.client_id != input.client_id) throw ClientException("Invalid client")

            if (authCode.expiration_date.isBefore(OffsetDateTime.now())) {
                codesDb.deleteAuthCode(authCode.code)
                throw AuthorizationException("Invalid or expired code")
            }
        } catch (ex: NotFoundException) {
            throw AuthorizationException("Invalid or expired code")
        }
        codesDb.deleteAuthCode(authCode.code)

        val accessToken = generateAndCreateUniqueAccessToken(authCode)
        return AccessToken(
            access_token = accessToken.code,
            expires_in = ChronoUnit.SECONDS.between(OffsetDateTime.now(), accessToken.expirationDate)
        ).toResponseEntity(HttpStatus.OK)
    }

    private fun generateAndCreateUniqueAccessToken(authCode: AuthCode): CodeWrapper {
        for (i in 0 until NUMBER_OF_RETRIES) {
            val accessToken = cryptoUtils.generateAccessToken()
            try {
                accessTokensDb.createAccessToken(
                    cryptoUtils.hash(accessToken.code),
                    accessToken.expirationDate,
                    authCode.user_id,
                    authCode.client_id
                )

                return accessToken
            } catch (ex: JdbiException) {
                if (ex.getPsqlErrorCode() != PsqlErrorCode.UniqueViolation) throw ex
                // If token was not unique, the loop will repeat and generate a new one
            }
        }
        throw ServerErrorException("Number of retries exceeded while trying to generate an unique access token")
    }

    @PostMapping(AUTH_REVOKE_HREF)
    fun revokeAccessToken(
        input: RevocationInputModel?
    ) : ResponseEntity<Any> {
        if (input == null) throw InvalidInputException("Missing body")
        if (input.client_id == null) throw InvalidInputException("Missing client_id")
        if (input.client_secret == null) throw InvalidInputException("Missing client_secret")
        if (input.token == null) throw InvalidInputException("Missing token to revoke")

        val client = clientsDb.getClientById(input.client_id)
        if (!cryptoUtils.validateHash(input.client_secret, client.secret)) {
            throw ClientException("Invalid client")
        }

        val hashedToken = cryptoUtils.hash(input.token)
        val token: org.ionproject.codegarten.database.dto.AccessToken
        try {
            token = accessTokensDb.getAccessToken(hashedToken)
            if (token.client_id != input.client_id) throw ClientException("Invalid client")
        } catch (ex: NotFoundException) {
            throw AuthorizationException("Invalid token")
        }

        accessTokensDb.deleteAccessToken(hashedToken)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }
}