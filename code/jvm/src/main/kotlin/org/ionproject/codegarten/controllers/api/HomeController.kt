package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.API_BASE_URI
import org.ionproject.codegarten.Routes.HOST
import org.ionproject.codegarten.Routes.ORGS_HREF
import org.ionproject.codegarten.Routes.SCHEME
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.controllers.models.HomeOutputModel
import org.ionproject.codegarten.responses.Response
import org.ionproject.codegarten.responses.siren.SirenLink
import org.ionproject.codegarten.responses.toResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.management.ManagementFactory
import java.net.URI
import java.time.OffsetDateTime

@RestController
class HomeController {

    private val runTimeBean = ManagementFactory.getRuntimeMXBean()

    @GetMapping(API_BASE_URI)
    fun getHome(): ResponseEntity<Response> {
        val hostString = "${SCHEME.toLowerCase()}://${HOST}"

        return HomeOutputModel(
            name = "i-on CodeGarten",
            description = "CodeGarten is a system to create and manage Git repos used by students while working on course assignments",
            time = OffsetDateTime.now(),
            uptimeMs = runTimeBean.uptime
        ).toSirenObject(
            links = listOf(
                SirenLink(listOf("organizations"), URI(ORGS_HREF).includeHost()),
                // TODO: Add all SirenLink Templates
            ),
        ).toResponseEntity(HttpStatus.OK)
    }
}