package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.API_BASE_URI
import org.ionproject.codegarten.Routes.ASSIGNMENTS_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.ASSIGNMENT_BY_NUMBER_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.CLASSROOMS_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.CLASSROOM_BY_NUMBER_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.DELIVERIES_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.DELIVERIES_OF_PARTICIPANT_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.DELIVERY_BY_NUMBER_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.ORGS_HREF
import org.ionproject.codegarten.Routes.ORG_BY_ID_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.PARTICIPANTS_OF_ASSIGNMENT_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.PARTICIPATION_IN_ASSIGNMENT_OF_USER_TEMPLATE
import org.ionproject.codegarten.Routes.PARTICIPATION_IN_CLASSROOM_OF_USER_TEMPLATE
import org.ionproject.codegarten.Routes.TEAMS_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.TEAM_BY_NUMBER_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.USERS_OF_CLASSROOM_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.USERS_OF_TEAM_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.USER_BY_ID_HREF_TEMPLATE
import org.ionproject.codegarten.Routes.USER_HREF
import org.ionproject.codegarten.Routes.USER_INVITE_CLASSROOM_TEAMS_TEMPLATE
import org.ionproject.codegarten.Routes.USER_INVITE_HREF_TEMPLATE
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
        return HomeOutputModel(
            name = "i-on CodeGarten",
            description = "CodeGarten is a system to create and manage Git repos used by students while working on course assignments",
            time = OffsetDateTime.now(),
            uptimeMs = runTimeBean.uptime,
            authors = listOf("Diogo Sousa LEIC 20/21", "João Moura LEIC 20/21", "Tiago David LEIC 20/21")
        ).toSirenObject(
            links = listOf(
                SirenLink(listOf("organizations"), URI(ORGS_HREF)),
                SirenLink(listOf("organization"), hrefTemplate = ORG_BY_ID_HREF_TEMPLATE),

                SirenLink(listOf("classrooms"), hrefTemplate = CLASSROOMS_HREF_TEMPLATE),
                SirenLink(listOf("classroom"), hrefTemplate = CLASSROOM_BY_NUMBER_HREF_TEMPLATE),
                SirenLink(listOf("classroomUsers"), hrefTemplate = USERS_OF_CLASSROOM_HREF_TEMPLATE),
                SirenLink(listOf("classroomTeams"), hrefTemplate = TEAMS_HREF_TEMPLATE),
                SirenLink(listOf("classroomTeam"), hrefTemplate = TEAM_BY_NUMBER_HREF_TEMPLATE),
                SirenLink(listOf("classroomTeamUsers"), hrefTemplate = USERS_OF_TEAM_HREF_TEMPLATE),

                SirenLink(listOf("assignments"), hrefTemplate = ASSIGNMENTS_HREF_TEMPLATE),
                SirenLink(listOf("assignment"), hrefTemplate = ASSIGNMENT_BY_NUMBER_HREF_TEMPLATE),
                SirenLink(listOf("assignmentParticipants"), hrefTemplate = PARTICIPANTS_OF_ASSIGNMENT_HREF_TEMPLATE),

                SirenLink(listOf("deliveries"), hrefTemplate = DELIVERIES_HREF_TEMPLATE),
                SirenLink(listOf("delivery"), hrefTemplate = DELIVERY_BY_NUMBER_HREF_TEMPLATE),
                SirenLink(listOf("participantDeliveries"), hrefTemplate = DELIVERIES_OF_PARTICIPANT_HREF_TEMPLATE),

                SirenLink(listOf("user"), hrefTemplate = USER_BY_ID_HREF_TEMPLATE),
                SirenLink(listOf("authenticatedUser"), URI(USER_HREF)),

                SirenLink(listOf("invitation"), hrefTemplate = USER_INVITE_HREF_TEMPLATE),
                SirenLink(listOf("invitationTeams"), hrefTemplate = USER_INVITE_CLASSROOM_TEAMS_TEMPLATE),

                SirenLink(listOf("classroomParticipation"), hrefTemplate = PARTICIPATION_IN_CLASSROOM_OF_USER_TEMPLATE),
                SirenLink(listOf("assignmentParticipation"), hrefTemplate = PARTICIPATION_IN_ASSIGNMENT_OF_USER_TEMPLATE),
            )
        ).toResponseEntity(HttpStatus.OK)
    }
}