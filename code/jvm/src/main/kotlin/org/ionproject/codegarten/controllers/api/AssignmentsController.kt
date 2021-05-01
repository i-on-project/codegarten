package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.ASSIGNMENTS_HREF
import org.ionproject.codegarten.Routes.ASSIGNMENT_BY_NUMBER_HREF
import org.ionproject.codegarten.Routes.ASSIGNMENT_PARAM
import org.ionproject.codegarten.Routes.CLASSROOM_PARAM
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.Routes.SELF_PARAM
import org.ionproject.codegarten.Routes.createSirenLinkListForPagination
import org.ionproject.codegarten.Routes.getAssignmentByNumberUri
import org.ionproject.codegarten.Routes.getAssignmentsUri
import org.ionproject.codegarten.Routes.getClassroomByNumberUri
import org.ionproject.codegarten.Routes.getDeliveriesUri
import org.ionproject.codegarten.Routes.getOrgByIdUri
import org.ionproject.codegarten.Routes.getUsersOfAssignmentUri
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.controllers.api.actions.AssignmentActions
import org.ionproject.codegarten.controllers.api.actions.AssignmentActions.getDeleteAssignmentAction
import org.ionproject.codegarten.controllers.api.actions.AssignmentActions.getEditAssignmentAction
import org.ionproject.codegarten.controllers.models.AssignmentCreateInputModel
import org.ionproject.codegarten.controllers.models.AssignmentEditInputModel
import org.ionproject.codegarten.controllers.models.AssignmentOutputModel
import org.ionproject.codegarten.controllers.models.AssignmentsOutputModel
import org.ionproject.codegarten.controllers.models.validAssignmentTypes
import org.ionproject.codegarten.database.dto.Assignment
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserClassroom
import org.ionproject.codegarten.database.dto.UserClassroomMembership
import org.ionproject.codegarten.database.helpers.AssignmentsDb
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserAuth
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInAssignment
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInClassroom
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.responses.Response
import org.ionproject.codegarten.responses.siren.SirenAction
import org.ionproject.codegarten.responses.siren.SirenLink
import org.ionproject.codegarten.responses.toResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AssignmentsController(
    val assignmentsDb: AssignmentsDb,
    val gitHub: GitHubInterface,
) {

    @RequiresUserInClassroom
    @GetMapping(ASSIGNMENTS_HREF)
    fun getUserAssignmentsFromClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        pagination: Pagination,
        user: User,
        userClassroom: UserClassroom,
    ): ResponseEntity<Response> {
        val assignmentsCount: Int
        val actions: List<SirenAction>?

        val assignments = when (userClassroom.role) {
            UserClassroomMembership.TEACHER -> {
                actions = listOf(
                    AssignmentActions.getCreateAssignmentAction(orgId, classroomNumber)
                )
                assignmentsCount = assignmentsDb.getAllAssignmentsCount(orgId, classroomNumber)
                assignmentsDb.getAllAssignments(orgId, classroomNumber, pagination.page, pagination.limit)
            }
            UserClassroomMembership.STUDENT -> {
                actions = null
                assignmentsCount = assignmentsDb.getAssignmentsOfUserCount(orgId, classroomNumber, user.uid)
                assignmentsDb.getAssignmentsOfUser(orgId, classroomNumber, user.uid, pagination.page, pagination.limit)
            }
            else -> throw AuthorizationException("User is not a member of the classroom")
        }

        val org = gitHub.getOrgById(orgId, user.gh_token)
        return AssignmentsOutputModel(
            collectionSize = assignmentsCount,
            pageIndex = pagination.page,
            pageSize = assignments.size,
        ).toSirenObject(
            entities = assignments.map {
                AssignmentOutputModel(
                    id = it.aid,
                    name = it.name,
                    description = it.description,
                    type = it.type,
                    repoPrefix = it.repo_prefix,
                    repoTemplate = it.template,
                    classroom = it.classroom_name,
                    organization = org.login
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM), getAssignmentByNumberUri(orgId, classroomNumber, it.number).includeHost()),
                        SirenLink(listOf("deliveries"), getDeliveriesUri(orgId, classroomNumber, it.number).includeHost()),
                        SirenLink(listOf("users"), getUsersOfAssignmentUri(orgId, classroomNumber, it.number).includeHost()),
                        SirenLink(listOf("assignments"), getAssignmentsUri(orgId, classroomNumber).includeHost()),
                        SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                        SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()),
                    )
                )
            },
            actions = actions,
            links = createSirenLinkListForPagination(
                getAssignmentsUri(orgId, classroomNumber).includeHost(),
                pagination.page,
                pagination.limit,
                assignmentsCount
            ) + listOf(
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost())
            )
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserInAssignment
    @RequiresUserAuth
    @GetMapping(ASSIGNMENT_BY_NUMBER_HREF)
    fun getAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        user: User,
        userClassroom: UserClassroom,
        assignment: Assignment,
    ): ResponseEntity<Response> {
        val actions = when (userClassroom.role) {
            UserClassroomMembership.TEACHER -> {
                listOf(
                    getEditAssignmentAction(orgId, classroomNumber, assignmentNumber),
                    getDeleteAssignmentAction(orgId, classroomNumber, assignmentNumber)
                )
            }
            UserClassroomMembership.STUDENT -> { listOf() }
            else -> throw AuthorizationException("User is not a member of the classroom")
        }

        val org = gitHub.getOrgById(orgId, user.gh_token)
        return AssignmentOutputModel(
            id = assignment.aid,
            name = assignment.name,
            description = assignment.description,
            type = assignment.type,
            repoPrefix = assignment.repo_prefix,
            repoTemplate = assignment.template,
            classroom = assignment.classroom_name,
            organization = org.login
        ).toSirenObject(
            actions = actions,
            links = listOf(
                SirenLink(listOf(SELF_PARAM), getAssignmentByNumberUri(orgId, classroomNumber, assignment.number).includeHost()),
                SirenLink(listOf("deliveries"), getDeliveriesUri(orgId, classroomNumber, assignment.number).includeHost()),
                SirenLink(listOf("users"), getUsersOfAssignmentUri(orgId, classroomNumber, assignment.number).includeHost()),
                SirenLink(listOf("assignments"), getAssignmentsUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()),
            )
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserInClassroom
    @PostMapping(ASSIGNMENTS_HREF)
    fun createAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User,
        userClassroom: UserClassroom,
        input: AssignmentCreateInputModel
    ): ResponseEntity<Any> {
        if (userClassroom.role != UserClassroomMembership.TEACHER) throw AuthorizationException("User is not a teacher")

        if (input.name == null) throw InvalidInputException("Missing name")
        if (input.type == null) throw InvalidInputException("Missing type")
        if (!validAssignmentTypes.contains(input.type)) throw InvalidInputException("Invalid type. Must be one of: $validAssignmentTypes")
        if (input.repoPrefix == null) throw InvalidInputException("Missing repoPrefix")

        val createdAssignment = assignmentsDb.createAssignment(orgId, classroomNumber,
            input.name, input.description, input.type, input.repoPrefix, input.repoTemplate
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location", getAssignmentByNumberUri(orgId, classroomNumber, createdAssignment.number).includeHost().toString())
            .body(null)
    }

    @RequiresUserInClassroom
    @PutMapping(ASSIGNMENT_BY_NUMBER_HREF)
    fun editAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        user: User,
        userClassroom: UserClassroom,
        input: AssignmentEditInputModel
    ): ResponseEntity<Any> {
        if (userClassroom.role != UserClassroomMembership.TEACHER) throw AuthorizationException("User is not a teacher")

        if (input.name == null && input.description == null) throw InvalidInputException("Missing name or description")
        assignmentsDb.editAssignment(orgId, classroomNumber, assignmentNumber, input.name, input.description)
        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Location", getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost().toString())
            .body(null)
    }

    @RequiresUserInClassroom
    @DeleteMapping(ASSIGNMENT_BY_NUMBER_HREF)
    fun deleteAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        user: User,
        userClassroom: UserClassroom,
    ): ResponseEntity<Any> {
        if (userClassroom.role != UserClassroomMembership.TEACHER) throw AuthorizationException("User is not a teacher")

        assignmentsDb.deleteAssignment(orgId, classroomNumber, assignmentNumber)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }
}