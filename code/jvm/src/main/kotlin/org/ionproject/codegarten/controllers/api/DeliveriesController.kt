package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.Routes.ASSIGNMENT_PARAM
import org.ionproject.codegarten.Routes.CLASSROOM_PARAM
import org.ionproject.codegarten.Routes.DELIVERIES_HREF
import org.ionproject.codegarten.Routes.DELIVERIES_OF_PARTICIPANT_HREF
import org.ionproject.codegarten.Routes.DELIVERY_BY_NUMBER_HREF
import org.ionproject.codegarten.Routes.DELIVERY_OF_PARTICIPANT_HREF
import org.ionproject.codegarten.Routes.DELIVERY_PARAM
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.Routes.PARTICIPANT_PARAM
import org.ionproject.codegarten.Routes.SELF_PARAM
import org.ionproject.codegarten.Routes.getAssignmentByNumberUri
import org.ionproject.codegarten.Routes.getClassroomByNumberUri
import org.ionproject.codegarten.Routes.getDeliveriesOfParticipantUri
import org.ionproject.codegarten.Routes.getDeliveriesUri
import org.ionproject.codegarten.Routes.getDeliveryByNumberUri
import org.ionproject.codegarten.Routes.getDeliveryOfParticipantUri
import org.ionproject.codegarten.Routes.getOrgByIdUri
import org.ionproject.codegarten.Routes.getTeamByNumberUri
import org.ionproject.codegarten.Routes.getUserByIdUri
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.controllers.api.actions.DeliveryActions.getCreateDeliveryAction
import org.ionproject.codegarten.controllers.api.actions.DeliveryActions.getDeleteDeliveryAction
import org.ionproject.codegarten.controllers.api.actions.DeliveryActions.getEditDeliveryAction
import org.ionproject.codegarten.controllers.models.DeliveriesOutputModel
import org.ionproject.codegarten.controllers.models.DeliveryCreateInputModel
import org.ionproject.codegarten.controllers.models.DeliveryEditInputModel
import org.ionproject.codegarten.controllers.models.DeliveryOutputModel
import org.ionproject.codegarten.controllers.models.ParticipantDeliveryItemOutputModel
import org.ionproject.codegarten.controllers.models.ParticipantDeliveryOutputModel
import org.ionproject.codegarten.database.dto.Assignment
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserClassroom
import org.ionproject.codegarten.database.dto.UserClassroomMembership.TEACHER
import org.ionproject.codegarten.database.dto.isGroupAssignment
import org.ionproject.codegarten.database.helpers.DeliveriesDb
import org.ionproject.codegarten.database.helpers.TeamsDb
import org.ionproject.codegarten.database.helpers.UsersDb
import org.ionproject.codegarten.exceptions.ForbiddenException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInAssignment
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGithubLoginUri
import org.ionproject.codegarten.responses.Response
import org.ionproject.codegarten.responses.siren.SirenLink
import org.ionproject.codegarten.responses.toResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

@RestController
class DeliveriesController(
    val deliveriesDb: DeliveriesDb,
    val usersDb: UsersDb,
    val teamsDb: TeamsDb,
    val gitHub: GitHubInterface
) {

    @RequiresUserInAssignment
    @GetMapping(DELIVERIES_HREF)
    fun getAllDeliveries(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        pagination: Pagination,
        user: User,
        userClassroom: UserClassroom,
        assignment: Assignment
    ): ResponseEntity<Response> {
        val deliveries = deliveriesDb.getDeliveriesOfAssignment(orgId, classroomNumber, assignmentNumber, pagination.page, pagination.limit)
        val deliveriesCount = deliveriesDb.getDeliveriesOfAssignmentCount(orgId, classroomNumber, assignmentNumber)
        val org = gitHub.getOrgById(orgId, user.gh_token)

        val actions = if (userClassroom.role == TEACHER) {
            listOf(getCreateDeliveryAction(orgId, classroomNumber, assignmentNumber))
        } else {
            null
        }

        return DeliveriesOutputModel(
            assignment = assignment.name,
            classroom = userClassroom.classroom.name,
            organization = org.login,
            collectionSize = deliveriesCount,
            pageIndex = pagination.page,
            pageSize = deliveries.size
        ).toSirenObject(
            entities = deliveries.map {
                DeliveryOutputModel(
                    id = it.did,
                    number = it.number,
                    tag = it.tag,
                    dueDate = it.due_date,
                    assignment = it.assignment_name,
                    classroom = it.classroom_name,
                    organization = org.login
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM), getDeliveryByNumberUri(orgId, classroomNumber, assignmentNumber, it.number).includeHost()),
                        SirenLink(listOf("deliveries"), getDeliveriesUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                        SirenLink(listOf("assignment"), getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                        SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                        SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()),
                        SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
                    )
                )
            },
            actions = actions,
            links = Routes.createSirenLinkListForPagination(
                Routes.getAssignmentsUri(orgId, classroomNumber).includeHost(),
                pagination.page,
                pagination.limit,
                deliveriesCount
            ) + listOf(
                SirenLink(listOf("assignment"), getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()) ,
                SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
            )
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserInAssignment
    @GetMapping(DELIVERY_BY_NUMBER_HREF)
    fun getDelivery(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = DELIVERY_PARAM) deliveryNumber: Int,
        user: User,
        userClassroom: UserClassroom,
    ): ResponseEntity<Response> {
        val delivery = deliveriesDb.getDeliveryByNumber(orgId, classroomNumber, assignmentNumber, deliveryNumber)
        val org = gitHub.getOrgById(orgId, user.gh_token)

        val actions = if (userClassroom.role == TEACHER) {
            listOf(
                getEditDeliveryAction(orgId, classroomNumber, assignmentNumber, deliveryNumber),
                getDeleteDeliveryAction(orgId, classroomNumber, assignmentNumber, deliveryNumber)
            )
        } else {
            null
        }

        return DeliveryOutputModel(
            id = delivery.did,
            number = delivery.number,
            tag = delivery.tag,
            dueDate = delivery.due_date,
            assignment = delivery.assignment_name,
            classroom = delivery.classroom_name,
            organization = org.login
        ).toSirenObject(
            actions = actions,
            links = listOf(
                SirenLink(listOf(SELF_PARAM), getDeliveryByNumberUri(orgId, classroomNumber, assignmentNumber, delivery.number).includeHost()),
                SirenLink(listOf("deliveries"), getDeliveriesUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                SirenLink(listOf("assignment"), getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()),
                SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
            )
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserInAssignment
    @GetMapping(DELIVERIES_OF_PARTICIPANT_HREF)
    fun getAllUserDeliveries(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = PARTICIPANT_PARAM) participantId: Int,
        pagination: Pagination,
        user: User,
        userClassroom: UserClassroom,
        assignment: Assignment
    ): ResponseEntity<Response> {
        val isGroupAssignment = assignment.isGroupAssignment()
        val isTeacher = userClassroom.role == TEACHER

        val repoId =
            if (isGroupAssignment) {
                val team = teamsDb.getTeam(orgId, classroomNumber, participantId)
                if (!isTeacher && !teamsDb.isUserInTeam(team.tid, user.uid))
                    throw ForbiddenException("Not enough permission to see deliveries")

                teamsDb.getTeamAssignment(assignment.aid, team.tid).repo_id
            } else {
                if (!isTeacher && user.uid != participantId)
                    throw ForbiddenException("Not enough permission to see deliveries")

                usersDb.getUserAssignment(orgId, classroomNumber, assignmentNumber, participantId).repo_id
            }

        val deliveries = deliveriesDb.getDeliveriesOfAssignment(orgId, classroomNumber, assignmentNumber, pagination.page, pagination.limit)
        val deliveriesCount = deliveriesDb.getDeliveriesOfAssignmentCount(orgId, classroomNumber, assignmentNumber)

        val ghTags = gitHub.getAllTagsFromRepo(repoId, user.gh_token)
        val org = gitHub.getOrgById(orgId, user.gh_token)

        return DeliveriesOutputModel(
            assignment = assignment.name,
            classroom = userClassroom.classroom.name,
            organization = org.login,
            collectionSize = deliveriesCount,
            pageIndex = pagination.page,
            pageSize = deliveries.size
        ).toSirenObject(
            entities = deliveries.map {
                ParticipantDeliveryItemOutputModel(
                    id = it.did,
                    number = it.number,
                    tag = it.tag,
                    dueDate = it.due_date,
                    isDelivered = ghTags.any { tag -> tag.name == it.tag },
                    assignment = it.assignment_name,
                    classroom = it.classroom_name,
                    organization = org.login
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM),
                            getDeliveryOfParticipantUri(orgId, classroomNumber, assignmentNumber, participantId, it.number).includeHost()),
                        SirenLink(listOf("participant-deliveries"),
                            getDeliveriesOfParticipantUri(orgId, classroomNumber, assignmentNumber, participantId).includeHost()),
                        SirenLink(listOf("deliveries"), getDeliveriesUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                        SirenLink(listOf("assignment"), getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                        SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                        SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()),
                        SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login)),
                        SirenLink(listOf("participant"),
                            if (isGroupAssignment) getUserByIdUri(participantId).includeHost()
                            else getTeamByNumberUri(orgId, classroomNumber, participantId)
                        )
                    )
                )
            },
            links = Routes.createSirenLinkListForPagination(
                Routes.getAssignmentsUri(orgId, classroomNumber).includeHost(),
                pagination.page,
                pagination.limit,
                deliveriesCount
            ) + listOf(
                SirenLink(listOf("deliveries"), getDeliveriesUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                SirenLink(listOf("assignment"), getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()),
                SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login)),
                SirenLink(listOf("participant"),
                    if (isGroupAssignment) getUserByIdUri(participantId).includeHost()
                    else getTeamByNumberUri(orgId, classroomNumber, participantId))
            )
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserInAssignment
    @GetMapping(DELIVERY_OF_PARTICIPANT_HREF)
    fun getUserDelivery(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = PARTICIPANT_PARAM) participantId: Int,
        @PathVariable(name = DELIVERY_PARAM) deliveryNumber: Int,
        user: User,
        userClassroom: UserClassroom,
        assignment: Assignment
    ): ResponseEntity<Response> {
        val isGroupAssignment = assignment.isGroupAssignment()
        val isTeacher = userClassroom.role == TEACHER

        val repoId =
            if (isGroupAssignment) {
                val team = teamsDb.getTeam(orgId, classroomNumber, participantId)
                if (!isTeacher && !teamsDb.isUserInTeam(team.tid, user.uid))
                    throw ForbiddenException("Not enough permission to see deliveries")

                teamsDb.getTeamAssignment(assignment.aid, team.tid).repo_id
            } else {
                if (!isTeacher && user.uid != participantId)
                    throw ForbiddenException("Not enough permission to see deliveries")

                usersDb.getUserAssignment(orgId, classroomNumber, assignmentNumber, participantId).repo_id
            }

        val delivery = deliveriesDb.getDeliveryByNumber(orgId, classroomNumber, assignmentNumber, deliveryNumber)
        val tag = gitHub.tryGetTagFromRepo(repoId, delivery.tag, user.gh_token)
        val org = gitHub.getOrgById(orgId, user.gh_token)

        return ParticipantDeliveryOutputModel(
            id = delivery.did,
            number = delivery.number,
            tag = delivery.tag,
            dueDate = delivery.due_date,
            isDelivered = tag.isPresent,
            deliverDate = if (tag.isEmpty) null else tag.get().date,
            assignment = delivery.assignment_name,
            classroom = delivery.classroom_name,
            organization = org.login
        ).toSirenObject(
            links = listOf(
                SirenLink(listOf(SELF_PARAM), getDeliveryOfParticipantUri(orgId, classroomNumber, assignmentNumber, participantId, deliveryNumber).includeHost()),
                SirenLink(listOf("participant-deliveries"), getDeliveriesOfParticipantUri(orgId, classroomNumber, assignmentNumber, participantId).includeHost()),
                SirenLink(listOf("deliveries"), getDeliveriesUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                SirenLink(listOf("assignment"), getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()),
                SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login)),
                SirenLink(listOf("participant"),
                    if (isGroupAssignment) getUserByIdUri(participantId).includeHost()
                    else getTeamByNumberUri(orgId, classroomNumber, participantId)
                )
            )
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserInAssignment
    @PostMapping(DELIVERIES_HREF)
    fun createDelivery(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        user: User,
        userClassroom: UserClassroom,
        @RequestBody input: DeliveryCreateInputModel?
    ): ResponseEntity<Any> {
        if (userClassroom.role != TEACHER) throw ForbiddenException("User is not a teacher")

        if (input == null) throw InvalidInputException("Missing body")
        if (input.tag == null) throw InvalidInputException("Missing tag")

        val dueDate = try {
            if (input.dueDate != null) OffsetDateTime.parse(input.dueDate)
            else null
        } catch (ex: DateTimeParseException) {
            throw InvalidInputException("Failed to parse due date")
        }

        val createdDelivery = deliveriesDb.createDelivery(orgId, classroomNumber, assignmentNumber, input.tag, dueDate)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location",
                getDeliveryByNumberUri(orgId, classroomNumber, assignmentNumber, createdDelivery.number).includeHost().toString())
            .body(null)
    }

    @RequiresUserInAssignment
    @PutMapping(DELIVERY_BY_NUMBER_HREF)
    fun editDelivery(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = DELIVERY_PARAM) deliveryNumber: Int,
        user: User,
        userClassroom: UserClassroom,
        @RequestBody input: DeliveryEditInputModel?
    ): ResponseEntity<Any> {
        if (userClassroom.role != TEACHER) throw ForbiddenException("User is not a teacher")

        if (input == null) throw InvalidInputException("Missing body")

        val dueDate = try {
            OffsetDateTime.parse(input.dueDate)
        } catch (ex: DateTimeParseException) {
            throw InvalidInputException("Failed to parse due date")
        }

        deliveriesDb.editDelivery(orgId, classroomNumber, assignmentNumber, deliveryNumber, input.tag, dueDate)

        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Location",
                getDeliveryByNumberUri(orgId, classroomNumber, assignmentNumber, deliveryNumber).includeHost().toString())
            .body(null)
    }

    @RequiresUserInAssignment
    @DeleteMapping(DELIVERY_BY_NUMBER_HREF)
    fun deleteDelivery(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = DELIVERY_PARAM) deliveryNumber: Int,
        user: User,
        userClassroom: UserClassroom
    ): ResponseEntity<Any> {
        if (userClassroom.role != TEACHER) throw ForbiddenException("User is not a teacher")

        deliveriesDb.deleteDelivery(orgId, classroomNumber, assignmentNumber, deliveryNumber)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }
}