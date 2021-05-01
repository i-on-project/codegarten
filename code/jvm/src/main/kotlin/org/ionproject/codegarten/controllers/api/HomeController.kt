package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.API_BASE_URI
import org.ionproject.codegarten.Routes.ASSIGNMENTS_HREF
import org.ionproject.codegarten.Routes.ASSIGNMENT_BY_NUMBER_HREF
import org.ionproject.codegarten.Routes.CLASSROOMS_HREF
import org.ionproject.codegarten.Routes.CLASSROOM_BY_NUMBER_HREF
import org.ionproject.codegarten.Routes.DELIVERIES_HREF
import org.ionproject.codegarten.Routes.DELIVERIES_OF_USER_HREF
import org.ionproject.codegarten.Routes.DELIVERY_BY_NUMBER_HREF
import org.ionproject.codegarten.Routes.HOST
import org.ionproject.codegarten.Routes.ORGS_HREF
import org.ionproject.codegarten.Routes.ORG_BY_ID_HREF
import org.ionproject.codegarten.Routes.SCHEME
import org.ionproject.codegarten.Routes.USERS_OF_ASSIGNMENT_HREF
import org.ionproject.codegarten.Routes.USERS_OF_CLASSROOM_HREF
import org.ionproject.codegarten.Routes.USER_BY_ID_HREF
import org.ionproject.codegarten.Routes.USER_HREF
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
                SirenLink(listOf("authenticatedUser"), URI(USER_HREF).includeHost()),
                SirenLink(listOf("organization"), hrefTemplate = "${hostString}${ORG_BY_ID_HREF}"),
                SirenLink(listOf("classrooms"), hrefTemplate = "${hostString}${CLASSROOMS_HREF}"),
                SirenLink(listOf("classroom"), hrefTemplate = "${hostString}${CLASSROOM_BY_NUMBER_HREF}"),
                SirenLink(listOf("classroomUsers"), hrefTemplate = "${hostString}${USERS_OF_CLASSROOM_HREF}"),
                SirenLink(listOf("assignments"), hrefTemplate = "${hostString}${ASSIGNMENTS_HREF}"),
                SirenLink(listOf("assignment"), hrefTemplate = "${hostString}${ASSIGNMENT_BY_NUMBER_HREF}"),
                SirenLink(listOf("assignmentUsers"), hrefTemplate = "${hostString}${USERS_OF_ASSIGNMENT_HREF}"),
                SirenLink(listOf("deliveries"), hrefTemplate = "${hostString}${DELIVERIES_HREF}"),
                SirenLink(listOf("delivery"), hrefTemplate = "${hostString}${DELIVERY_BY_NUMBER_HREF}"),
                SirenLink(listOf("user"), hrefTemplate = "${hostString}${USER_BY_ID_HREF}"),
                SirenLink(listOf("userDeliveries"), hrefTemplate = "${hostString}${DELIVERIES_OF_USER_HREF}"),
            )
        ).toResponseEntity(HttpStatus.OK)
    }
}