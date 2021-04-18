package org.ionproject.codegarten.controllers.im

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(AUTH_HREF)
class AuthController {

    @GetMapping("authorize")
    fun getAuthCode(
        @RequestParam client_id: String?,
        @RequestParam state: String?,
    ): ResponseEntity<Any> {
        TODO()
    }
}