package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.controllers.models.AuthorizationInputModel
import org.ionproject.codegarten.responses.Response
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(AUTH_HREF)
class AuthController : BaseApiController() {

    @PostMapping("access_token")
    fun getAccessToken(
        input: AuthorizationInputModel
    ) : ResponseEntity<Response> {
        TODO()
    }
}