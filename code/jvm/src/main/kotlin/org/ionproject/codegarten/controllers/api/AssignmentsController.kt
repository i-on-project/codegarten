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
import org.ionproject.codegarten.Routes.getParticipantsOfAssignmentUri
import org.ionproject.codegarten.controllers.api.actions.AssignmentActions
import org.ionproject.codegarten.controllers.api.actions.AssignmentActions.getDeleteAssignmentAction
import org.ionproject.codegarten.controllers.api.actions.AssignmentActions.getEditAssignmentAction
import org.ionproject.codegarten.controllers.models.AssignmentCreateInputModel
import org.ionproject.codegarten.controllers.models.AssignmentEditInputModel
import org.ionproject.codegarten.controllers.models.AssignmentItemOutputModel
import org.ionproject.codegarten.controllers.models.AssignmentOutputModel
import org.ionproject.codegarten.controllers.models.AssignmentsOutputModel
import org.ionproject.codegarten.controllers.models.validAssignmentTypes
import org.ionproject.codegarten.database.dto.Assignment
import org.ionproject.codegarten.database.dto.Installation
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserClassroom
import org.ionproject.codegarten.database.dto.UserClassroomMembership
import org.ionproject.codegarten.database.helpers.AssignmentsDb
import org.ionproject.codegarten.database.helpers.InviteCodesDb
import org.ionproject.codegarten.exceptions.ForbiddenException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.exceptions.NotFoundException
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresGhAppInstallation
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInAssignment
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInClassroom
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGithubLoginUri
import org.ionproject.codegarten.remote.github.responses.GitHubRepoResponse
import org.ionproject.codegarten.responses.Response
import org.ionproject.codegarten.responses.siren.SirenAction
import org.ionproject.codegarten.responses.siren.SirenLink
import org.ionproject.codegarten.responses.toResponseEntity
import org.ionproject.codegarten.utils.CryptoUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class AssignmentsController(
    val assignmentsDb: AssignmentsDb,
    val inviteCodesDb: InviteCodesDb,
    val gitHub: GitHubInterface,
    val cryptoUtils: CryptoUtils
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
        val actions: List<SirenAction>?

        val assignments = when (userClassroom.role) {
            UserClassroomMembership.TEACHER -> {
                actions = listOf(
                    AssignmentActions.getCreateAssignmentAction(orgId, classroomNumber)
                )
                assignmentsDb.getAllAssignments(orgId, classroomNumber, pagination.page, pagination.limit)
            }
            UserClassroomMembership.STUDENT -> {
                actions = null
                assignmentsDb.getAssignmentsOfUser(orgId, classroomNumber, user.uid, pagination.page, pagination.limit)
            }
            else -> throw ForbiddenException("User is not a member of the classroom")
        }

        val org = gitHub.getOrgById(orgId, user.gh_token)
        return AssignmentsOutputModel(
            classroom = userClassroom.classroom.name,
            organization = org.login,
            collectionSize = assignments.count,
            pageIndex = pagination.page,
            pageSize = assignments.results.size,
        ).toSirenObject(
            entities = assignments.results.map {
                AssignmentItemOutputModel(
                    id = it.aid,
                    inviteCode = if (userClassroom.role == UserClassroomMembership.TEACHER) it.inv_code else null,
                    number = it.number,
                    name = it.name,
                    description = it.description,
                    type = it.type,
                    classroom = it.classroom_name,
                    organization = org.login
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM), getAssignmentByNumberUri(orgId, classroomNumber, it.number)),
                        SirenLink(listOf("deliveries"), getDeliveriesUri(orgId, classroomNumber, it.number)),
                        SirenLink(listOf("participants"), getParticipantsOfAssignmentUri(orgId, classroomNumber, it.number)),
                        SirenLink(listOf("assignments"), getAssignmentsUri(orgId, classroomNumber)),
                        SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber)),
                        SirenLink(listOf("organization"), getOrgByIdUri(orgId)),
                        SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
                    )
                )
            },
            actions = actions,
            links = createSirenLinkListForPagination(
                getAssignmentsUri(orgId, classroomNumber),
                pagination.page,
                pagination.limit,
                assignments.count
            ) + listOf(
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber)),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId)),
                SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
            )
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserInAssignment
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
            else -> throw ForbiddenException("User is not a member of the classroom")
        }
        val org = gitHub.getOrgById(orgId, user.gh_token)

        val sirenLinks = mutableListOf(
            SirenLink(listOf(SELF_PARAM), getAssignmentByNumberUri(orgId, classroomNumber, assignment.number)),
            SirenLink(listOf("deliveries"), getDeliveriesUri(orgId, classroomNumber, assignment.number)),
            SirenLink(listOf("participants"), getParticipantsOfAssignmentUri(orgId, classroomNumber, assignment.number)),
            SirenLink(listOf("assignments"), getAssignmentsUri(orgId, classroomNumber)),
            SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber)),
            SirenLink(listOf("organization"), getOrgByIdUri(orgId)),
            SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
        )

        var repo: GitHubRepoResponse? = null
        if (assignment.repo_template != null) {
            try {
                repo = gitHub.getRepoById(assignment.repo_template, user.gh_token)
                sirenLinks.add(1, SirenLink(listOf("templateGitHub"), URI(repo.html_url)))
            }
            // If we can't get the template, we ignore the repository
            catch (e: NotFoundException) {}
            catch (e: ForbiddenException) {}
        }

        return AssignmentOutputModel(
            id = assignment.aid,
            inviteCode = if (userClassroom.role == UserClassroomMembership.TEACHER) assignment.inv_code else null,
            number = assignment.number,
            name = assignment.name,
            description = assignment.description,
            type = assignment.type,
            repoPrefix = assignment.repo_prefix,
            repoTemplate = repo?.name,
            classroom = assignment.classroom_name,
            organization = org.login
        ).toSirenObject(
            actions = actions,
            links = sirenLinks
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserInClassroom
    @RequiresGhAppInstallation
    @PostMapping(ASSIGNMENTS_HREF)
    fun createAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User,
        userClassroom: UserClassroom,
        installation: Installation,
        @RequestBody input: AssignmentCreateInputModel?
    ): ResponseEntity<Response> {
        if (userClassroom.role != UserClassroomMembership.TEACHER) throw ForbiddenException("User is not a teacher")

        if (input == null) throw InvalidInputException("Missing body")
        if (input.name == null) throw InvalidInputException("Missing name")
        if (input.type == null) throw InvalidInputException("Missing type")
        if (!validAssignmentTypes.contains(input.type)) throw InvalidInputException("Invalid type. Must be one of: $validAssignmentTypes")
        if (input.repoPrefix == null) throw InvalidInputException("Missing repoPrefix")

        val org = gitHub.getOrgById(orgId, installation.accessToken)

        var repoResponse: GitHubRepoResponse? = null
        if (input.repoTemplate != null) {
            val repo = try {
                gitHub.getRepoByName(org.login, input.repoTemplate, installation.accessToken)
            } catch (ex: Exception) {
                throw ForbiddenException("Repository '${input.repoTemplate}' is not in the organization")
            }

            if (!repo.is_template) throw ForbiddenException("Repository '${repo.name}' is not a template repository")
            repoResponse = repo
        }

        val createdAssignment = assignmentsDb.createAssignment(
            orgId, classroomNumber, input.name,
            input.description, input.type, input.repoPrefix, repoResponse?.id
        )

        val inviteCode = inviteCodesDb.generateAndCreateUniqueInviteCode(createdAssignment.classroom_id, createdAssignment.aid)

        val sirenLinks = mutableListOf(
            SirenLink(listOf(SELF_PARAM), getAssignmentByNumberUri(orgId, classroomNumber, createdAssignment.number)),
            SirenLink(listOf("deliveries"), getDeliveriesUri(orgId, classroomNumber, createdAssignment.number)),
            SirenLink(listOf("participants"), getParticipantsOfAssignmentUri(orgId, classroomNumber, createdAssignment.number)),
            SirenLink(listOf("assignments"), getAssignmentsUri(orgId, classroomNumber)),
            SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber)),
            SirenLink(listOf("organization"), getOrgByIdUri(orgId)),
            SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
        )

        if (repoResponse != null) {
            sirenLinks.add(1, SirenLink(listOf("templateGitHub"), URI(repoResponse.html_url)))
        }

        return AssignmentOutputModel(
            id = createdAssignment.aid,
            inviteCode = inviteCode.inv_code,
            number = createdAssignment.number,
            name = createdAssignment.name,
            description = createdAssignment.description,
            type = createdAssignment.type,
            repoPrefix = createdAssignment.repo_prefix,
            repoTemplate = repoResponse?.name,
            classroom = createdAssignment.classroom_name,
            organization = org.login
        ).toSirenObject(
            actions = listOf(
                getEditAssignmentAction(orgId, classroomNumber, createdAssignment.number),
                getDeleteAssignmentAction(orgId, classroomNumber, createdAssignment.number)
            ),
            links = sirenLinks
        ).toResponseEntity(HttpStatus.CREATED, mapOf(
            "Location" to listOf(getAssignmentByNumberUri(orgId, classroomNumber, createdAssignment.number).toString())
        ))
    }

    @RequiresUserInClassroom
    @PutMapping(ASSIGNMENT_BY_NUMBER_HREF)
    fun editAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        user: User,
        userClassroom: UserClassroom,
        @RequestBody input: AssignmentEditInputModel?
    ): ResponseEntity<Any> {
        if (userClassroom.role != UserClassroomMembership.TEACHER) throw ForbiddenException("User is not a teacher")

        if (input == null) throw InvalidInputException("Missing body")
        if (input.name == null && input.description == null) throw InvalidInputException("Missing name or description")

        assignmentsDb.editAssignment(orgId, classroomNumber, assignmentNumber, input.name, input.description)
        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Location", getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).toString())
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
        if (userClassroom.role != UserClassroomMembership.TEACHER) throw ForbiddenException("User is not a teacher")

        assignmentsDb.deleteAssignment(orgId, classroomNumber, assignmentNumber)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }
}