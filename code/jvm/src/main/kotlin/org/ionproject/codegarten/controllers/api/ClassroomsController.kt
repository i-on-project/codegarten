package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.CLASSROOMS_HREF
import org.ionproject.codegarten.Routes.CLASSROOM_BY_NUMBER_HREF
import org.ionproject.codegarten.Routes.CLASSROOM_PARAM
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.Routes.SELF_PARAM
import org.ionproject.codegarten.Routes.createSirenLinkListForPagination
import org.ionproject.codegarten.Routes.getAssignmentsUri
import org.ionproject.codegarten.Routes.getClassroomByNumberUri
import org.ionproject.codegarten.Routes.getClassroomsUri
import org.ionproject.codegarten.Routes.getOrgByIdUri
import org.ionproject.codegarten.Routes.getTeamsUri
import org.ionproject.codegarten.Routes.getUsersOfClassroomUri
import org.ionproject.codegarten.controllers.api.actions.ClassroomActions.getCreateClassroomAction
import org.ionproject.codegarten.controllers.api.actions.ClassroomActions.getDeleteClassroomAction
import org.ionproject.codegarten.controllers.api.actions.ClassroomActions.getEditClassroomAction
import org.ionproject.codegarten.controllers.models.ClassroomCreateInputModel
import org.ionproject.codegarten.controllers.models.ClassroomEditInputModel
import org.ionproject.codegarten.controllers.models.ClassroomOutputModel
import org.ionproject.codegarten.controllers.models.ClassroomsOutputModel
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserClassroom
import org.ionproject.codegarten.database.dto.UserClassroomMembership.TEACHER
import org.ionproject.codegarten.database.helpers.ClassroomsDb
import org.ionproject.codegarten.database.helpers.InviteCodesDb
import org.ionproject.codegarten.database.helpers.UsersDb
import org.ionproject.codegarten.exceptions.ForbiddenException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresGhAppInstallation
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInClassroom
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInOrg
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGithubLoginUri
import org.ionproject.codegarten.remote.github.responses.GitHubUserOrgRole
import org.ionproject.codegarten.responses.Response
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

@RestController
class ClassroomsController(
    val classroomsDb: ClassroomsDb,
    val inviteCodesDb: InviteCodesDb,
    val usersDb: UsersDb,
    val gitHub: GitHubInterface,
    val cryptoUtils: CryptoUtils
) {

    @RequiresUserInOrg
    @GetMapping(CLASSROOMS_HREF)
    fun getUserClassrooms(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        pagination: Pagination,
        user: User,
        orgRole: GitHubUserOrgRole
    ): ResponseEntity<Response> {
        val classrooms = classroomsDb.getClassroomsOfUser(orgId, user.uid, pagination.page, pagination.limit)
        val org = gitHub.getOrgById(orgId, user.gh_token)

        val isOrgAdmin = orgRole == GitHubUserOrgRole.ADMIN

        val actions =
            if (isOrgAdmin)
                listOf(getCreateClassroomAction(orgId))
            else
                null

        return ClassroomsOutputModel(
            organization = org.login,
            collectionSize = classrooms.count,
            pageIndex = pagination.page,
            pageSize = classrooms.results.size,
        ).toSirenObject(
            entities = classrooms.results.map {
                ClassroomOutputModel(
                    id = it.cid,
                    inviteCode = if (isOrgAdmin) it.inv_code else null,
                    number = it.number,
                    name = it.name,
                    description = it.description,
                    organization = org.login
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM), getClassroomByNumberUri(orgId, it.number)),
                        SirenLink(listOf("assignments"), getAssignmentsUri(orgId, it.number)),
                        SirenLink(listOf("teams"), getTeamsUri(orgId, it.number)),
                        SirenLink(listOf("users"), getUsersOfClassroomUri(orgId, it.number)),
                        SirenLink(listOf("classrooms"), getClassroomsUri(orgId)),
                        SirenLink(listOf("organization"), getOrgByIdUri(orgId)),
                        SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
                    )
                )
            },
            actions = actions,
            links = createSirenLinkListForPagination(
                getClassroomsUri(orgId),
                pagination.page,
                pagination.limit,
                classrooms.count
            ) + listOf(
                SirenLink(listOf("organization"), getOrgByIdUri(orgId)),
                SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
            )
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserInClassroom
    @GetMapping(CLASSROOM_BY_NUMBER_HREF)
    fun getClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User,
        orgRole: GitHubUserOrgRole,
        userClassroom: UserClassroom
    ): ResponseEntity<Response> {
        val classroom = userClassroom.classroom
        val org = gitHub.getOrgById(orgId, user.gh_token)

        val isTeacher = userClassroom.role == TEACHER

        val actions =
            if (isTeacher)
                listOf(
                    getEditClassroomAction(orgId, classroom.number),
                    getDeleteClassroomAction(orgId, classroom.number)
                )
            else
                null

        return ClassroomOutputModel(
            id = classroom.cid,
            inviteCode = if (isTeacher) classroom.inv_code else null,
            number = classroom.number,
            name = classroom.name,
            description = classroom.description,
            organization = org.login
        ).toSirenObject(
            actions = actions,
            links = listOf(
                SirenLink(listOf(SELF_PARAM), getClassroomByNumberUri(orgId, classroom.number)),
                SirenLink(listOf("assignments"), getAssignmentsUri(orgId, classroom.number)),
                SirenLink(listOf("teams"), getTeamsUri(orgId, classroom.number)),
                SirenLink(listOf("users"), getUsersOfClassroomUri(orgId, classroom.number)),
                SirenLink(listOf("classrooms"), getClassroomsUri(orgId)),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId)),
                SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
            )
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresGhAppInstallation
    @RequiresUserInOrg
    @PostMapping(CLASSROOMS_HREF)
    fun createClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        user: User,
        orgMembership: GitHubUserOrgRole,
        @RequestBody input: ClassroomCreateInputModel?
    ): ResponseEntity<Response> {
        if (orgMembership != GitHubUserOrgRole.ADMIN) throw ForbiddenException("User is not an organization admin")

        if (input == null) throw InvalidInputException("Missing body")
        if (input.name == null) throw InvalidInputException("Missing name")

        val org = gitHub.getOrgById(orgId, user.gh_token)

        val createdClassroom = classroomsDb.createClassroom(orgId, input.name, input.description)
        val inviteCode = inviteCodesDb.generateAndCreateUniqueInviteCode(createdClassroom.cid)

        usersDb.addUserToClassroom(createdClassroom.cid, user.uid, TEACHER)

        return ClassroomOutputModel(
            id = createdClassroom.cid,
            inviteCode = inviteCode.inv_code,
            number = createdClassroom.number,
            name = createdClassroom.name,
            description = createdClassroom.description,
            organization = org.login
        ).toSirenObject(
            actions = listOf(
                getEditClassroomAction(orgId, createdClassroom.number),
                getDeleteClassroomAction(orgId, createdClassroom.number)
            ),
            links = listOf(
                SirenLink(listOf(SELF_PARAM), getClassroomByNumberUri(orgId, createdClassroom.number)),
                SirenLink(listOf("assignments"), getAssignmentsUri(orgId, createdClassroom.number)),
                SirenLink(listOf("teams"), getTeamsUri(orgId, createdClassroom.number)),
                SirenLink(listOf("users"), getUsersOfClassroomUri(orgId, createdClassroom.number)),
                SirenLink(listOf("classrooms"), getClassroomsUri(orgId)),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId)),
                SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
            )
        ).toResponseEntity(HttpStatus.CREATED,
            mapOf(
                "Location" to listOf(getClassroomByNumberUri(orgId, createdClassroom.number).toString())
            )
        )
    }

    @RequiresUserInClassroom
    @PutMapping(CLASSROOM_BY_NUMBER_HREF)
    fun editClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User,
        userClassroom: UserClassroom,
        @RequestBody input: ClassroomEditInputModel?
    ): ResponseEntity<Any> {
        if (userClassroom.role != TEACHER) throw ForbiddenException("User is not a classroom teacher")

        if (input == null) throw InvalidInputException("Missing body")
        if (input.name == null && input.description == null) throw InvalidInputException("Missing name or description")

        classroomsDb.editClassroom(orgId, classroomNumber, input.name, input.description)
        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Location", getClassroomByNumberUri(orgId, classroomNumber).toString())
            .body(null)
    }

    @RequiresUserInClassroom
    @DeleteMapping(CLASSROOM_BY_NUMBER_HREF)
    fun deleteClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User,
        userClassroom: UserClassroom
    ): ResponseEntity<Any> {
        if (userClassroom.role != TEACHER) throw ForbiddenException("User is not a classroom teacher")

        classroomsDb.deleteClassroom(orgId, classroomNumber)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }
}