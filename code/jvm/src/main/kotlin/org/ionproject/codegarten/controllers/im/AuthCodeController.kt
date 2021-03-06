package org.ionproject.codegarten.controllers.im

import org.ionproject.codegarten.Routes.AUTH_CODE_CB_HREF
import org.ionproject.codegarten.Routes.AUTH_CODE_HREF
import org.ionproject.codegarten.Routes.CLIENT_ID_PARAM
import org.ionproject.codegarten.Routes.CODE_PARAM
import org.ionproject.codegarten.Routes.ERR_PARAM
import org.ionproject.codegarten.Routes.STATE_PARAM
import org.ionproject.codegarten.database.PsqlErrorCode
import org.ionproject.codegarten.database.dto.Client
import org.ionproject.codegarten.database.getPsqlErrorCode
import org.ionproject.codegarten.database.helpers.AuthCodesDb
import org.ionproject.codegarten.database.helpers.ClientsDb
import org.ionproject.codegarten.database.helpers.UsersDb
import org.ionproject.codegarten.exceptions.ClientException
import org.ionproject.codegarten.exceptions.HttpRequestException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.exceptions.NotFoundException
import org.ionproject.codegarten.exceptions.ServerErrorException
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.utils.CryptoUtils
import org.jdbi.v3.core.JdbiException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private const val NUMBER_OF_RETRIES = 10 // Used when generating unique auth codes

@RestController
class AuthCodeController(
    val gitHub: GitHubInterface,
    val clientsDb: ClientsDb,
    val authCodesDb: AuthCodesDb,
    val usersDb: UsersDb,
    val cryptoUtils: CryptoUtils
) {

    @GetMapping(AUTH_CODE_HREF)
    fun getAuthCode(
        @RequestParam(name = CLIENT_ID_PARAM) clientId: Int?,
        @RequestParam(name = STATE_PARAM) state: String?,
    ): ResponseEntity<Any> {
        if (clientId == null) throw ClientException("client_id not provided")
        try {
            clientsDb.getClientById(clientId)
        } catch (ex: NotFoundException) {
            throw ClientException("Invalid client")
        }

        var stateToSend = "$clientId:"
        if (state != null) {
            stateToSend += state
        }

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header("Location", gitHub.getAuthUri(stateToSend).toString())
            .body(null)
    }

    @GetMapping(AUTH_CODE_CB_HREF)
    fun authCallback(
        @RequestParam(name = CODE_PARAM) gitHubCode: String?,
        @RequestParam(name = ERR_PARAM) error: String?,
        @RequestParam(name = STATE_PARAM) state: String
    ): ResponseEntity<Any> {
        val clientId = state.substringBefore(":")
        val stateToSend = state.substringAfter(":")

        val client: Client
        try {
            client = clientsDb.getClientById(clientId.toInt())
        } catch (ex: NotFoundException) {
            throw ClientException("Invalid client")
        }

        var redirectUri = client.redirect_uri

        if (error != null) {
            redirectUri += "?$ERR_PARAM=$error"
        } else {
            val accessToken: String
            try {
                accessToken = gitHub.getAccessTokenFromAuthCode(gitHubCode!!).access_token
            } catch (ex: HttpRequestException) {
                throw InvalidInputException("Invalid GitHub auth code")
            }

            val ghUser = gitHub.getUserInfo(accessToken)

            val cgUserId = usersDb.createOrUpdateUser(
                ghUser.login,
                ghUser.id,
                cryptoUtils.encrypt(accessToken)
            )

            val code = generateAndCreateUniqueAuthCode(cgUserId, clientId.toInt())
            redirectUri += "?$CODE_PARAM=${code}"
        }

        if (stateToSend.isNotEmpty()) {
            redirectUri += "&$STATE_PARAM=$stateToSend"
        }

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header("Location", redirectUri)
            .body(null)
    }

    private fun generateAndCreateUniqueAuthCode(cgUserId: Int, clientId: Int): String {
        for (i in 0 until NUMBER_OF_RETRIES) {
            val cgCodeWrapper = cryptoUtils.generateAuthCode()
            try {
                authCodesDb.createAuthCode(
                    cgCodeWrapper.code,
                    cgCodeWrapper.expirationDate,
                    cgUserId,
                    clientId
                )

                return cgCodeWrapper.code
            } catch (ex: JdbiException) {
                if (ex.getPsqlErrorCode() != PsqlErrorCode.UniqueViolation) throw ex
                // If code was not unique, the loop will repeat and generate a new one
            }
        }
        throw ServerErrorException("Number of retries exceeded while trying to generate an unique auth code")
    }
}