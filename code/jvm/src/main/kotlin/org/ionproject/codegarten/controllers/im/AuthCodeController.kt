package org.ionproject.codegarten.controllers.im

import org.ionproject.codegarten.Routes.AUTH_CODE_CB_HREF
import org.ionproject.codegarten.Routes.AUTH_CODE_HREF
import org.ionproject.codegarten.Routes.CLIENT_ID_PARAM
import org.ionproject.codegarten.Routes.CODE_PARAM
import org.ionproject.codegarten.Routes.ERR_PARAM
import org.ionproject.codegarten.Routes.STATE_PARAM
import org.ionproject.codegarten.auth.OAuthUtils
import org.ionproject.codegarten.database.dto.Client
import org.ionproject.codegarten.database.helpers.AuthCodesDb
import org.ionproject.codegarten.database.helpers.ClientsDb
import org.ionproject.codegarten.database.helpers.UsersDb
import org.ionproject.codegarten.exceptions.ClientException
import org.ionproject.codegarten.exceptions.NotFoundException
import org.ionproject.codegarten.remote.GitHubInterface
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthCodeController(
    val github: GitHubInterface,
    val clientsDb: ClientsDb,
    val authCodesDb: AuthCodesDb,
    val usersDb: UsersDb,
) {
    // TODO: Argument resolver for request parameters

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
        if (state != null) stateToSend += state

        return ResponseEntity
            .status(302)
            .header("Location", github.getAuthEndpoint(stateToSend).toString())
            .body(null)
    }

    @GetMapping(AUTH_CODE_CB_HREF)
    fun authCallback(
        @RequestParam(name = CODE_PARAM) githubCode: String?,
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
            val githubUser = github.getUserFromAuthCode(githubCode!!)
            val cgUser = usersDb.createUser(githubUser.username, githubUser.userId, githubUser.accessToken)

            val cgCode = OAuthUtils.generateAuthCode()
            authCodesDb.createAuthCode(
                cgCode.code,
                cgCode.expirationDate,
                cgUser.uid,
                clientId.toInt()
            )
            redirectUri += "?$CODE_PARAM=$cgCode"
        }

        if (stateToSend.isNotEmpty()) {
            redirectUri += "&$STATE_PARAM=$stateToSend"
        }

        return ResponseEntity
            .status(302)
            .header("Location", redirectUri)
            .body(null)
    }

}