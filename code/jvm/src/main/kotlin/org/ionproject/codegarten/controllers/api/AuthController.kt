package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.AUTH_TOKEN_HREF
import org.ionproject.codegarten.controllers.models.AuthorizationInputModel
import org.ionproject.codegarten.database.helpers.AuthCodesDb
import org.ionproject.codegarten.responses.Response
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(db: AuthCodesDb) {

    @PostMapping(AUTH_TOKEN_HREF)
    fun getAccessToken(
        input: AuthorizationInputModel
    ) : ResponseEntity<Response> {
        TODO()
    }
}