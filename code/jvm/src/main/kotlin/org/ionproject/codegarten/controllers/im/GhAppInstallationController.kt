package org.ionproject.codegarten.controllers.im

import org.ionproject.codegarten.Routes.GH_INSTALLATIONS_CB_HREF
import org.ionproject.codegarten.Routes.GH_INSTALLATIONS_HREF
import org.ionproject.codegarten.remote.GitHubInterface
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class GhAppInstallationController(val github: GitHubInterface) {

    @GetMapping(GH_INSTALLATIONS_HREF)
    fun installToOrg() : ResponseEntity<Any> {
        TODO()
    }

    @GetMapping(GH_INSTALLATIONS_CB_HREF)
    fun orgInstallationCallback(
        @RequestParam installation_id: Int
    ) : ResponseEntity<Any> {
        TODO()
    }
}