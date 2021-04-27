package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.AUTH_TOKEN_HREF
import org.ionproject.codegarten.auth.AuthUtils
import org.ionproject.codegarten.controllers.models.AuthorizationInputModel
import org.ionproject.codegarten.database.dto.AuthCode
import org.ionproject.codegarten.database.helpers.AccessTokensDb
import org.ionproject.codegarten.database.helpers.AuthCodesDb
import org.ionproject.codegarten.database.helpers.ClientsDb
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.exceptions.NotFoundException
import org.ionproject.codegarten.responses.AccessToken
import org.ionproject.codegarten.responses.Response
import org.ionproject.codegarten.responses.toResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@RestController
class AuthController(
    val codesDb: AuthCodesDb,
    val accessTokensDb: AccessTokensDb,
    val clientsDb: ClientsDb,
    val authUtils: AuthUtils
) {

    @PostMapping(AUTH_TOKEN_HREF)
    fun getAccessToken(
        input: AuthorizationInputModel
    ) : ResponseEntity<Response> {
        if (input.client_id == null) throw InvalidInputException("Missing client_id")
        if (input.client_secret == null) throw InvalidInputException("Missing client_secret")
        if (input.code == null) throw InvalidInputException("Missing code")

        val authCode: AuthCode
        try {
            authCode = codesDb.getAuthCode(input.code)
            if (authCode.client_id != input.client_id) throw AuthorizationException("Invalid client")

            if (authCode.expiration_date.isBefore(OffsetDateTime.now())) {
                codesDb.deleteAuthCode(authCode.code)
                throw AuthorizationException("Invalid or expired code")
            }
        } catch (ex: NotFoundException) {
            throw AuthorizationException("Invalid or expired code")
        }

        val client = clientsDb.getClientById(authCode.client_id)
        if (!authUtils.validateClientSecret(input.client_secret, client.secret)) {
            throw AuthorizationException("Invalid client")
        }
        codesDb.deleteAuthCode(authCode.code)

        // TODO: Loop until insertion succeeds
        val accessToken = authUtils.generateAccessToken()
        accessTokensDb.createAccessToken(
            authUtils.hash(accessToken.code),
            accessToken.expirationDate,
            authCode.user_id,
            authCode.client_id
        )

        return AccessToken(
            access_token = accessToken.code,
            expires_in = ChronoUnit.SECONDS.between(OffsetDateTime.now(), accessToken.expirationDate)
        ).toResponseEntity(HttpStatus.OK)
    }
}