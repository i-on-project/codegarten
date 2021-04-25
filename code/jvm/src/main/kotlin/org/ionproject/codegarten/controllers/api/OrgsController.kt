package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.ORGS_HREF
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserAuth
import org.ionproject.codegarten.remote.GitHubInterface
import org.ionproject.codegarten.responses.Response
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OrgsController(
    gh: GitHubInterface
) {

    @RequiresUserAuth
    @GetMapping(ORGS_HREF)
    fun getUserOrgs(
        pagination: Pagination,
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }
}