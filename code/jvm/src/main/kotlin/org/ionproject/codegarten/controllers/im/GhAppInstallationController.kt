package org.ionproject.codegarten.controllers.im

import org.ionproject.codegarten.remote.GitHubInterface
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(GH_INSTALLATIONS_HREF)
class GhAppInstallationController(val github: GitHubInterface) : BaseImController() {

    @GetMapping
    fun installToOrg() : ResponseEntity<Any> {
        TODO()
    }

    @GetMapping("cb")
    fun orgInstallationCallback(
        @RequestParam installation_id: Int
    ) : ResponseEntity<Any> {
        TODO()
    }
}