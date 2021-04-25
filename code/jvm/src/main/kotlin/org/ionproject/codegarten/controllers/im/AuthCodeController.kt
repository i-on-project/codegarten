package org.ionproject.codegarten.controllers.im

import org.ionproject.codegarten.Routes.AUTH_CODE_HREF
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthCodeController {

    @GetMapping(AUTH_CODE_HREF)
    fun getAuthCode(
        @RequestParam client_id: String?,
        @RequestParam state: String?,
    ): ResponseEntity<Any> {
        TODO()
    }
}