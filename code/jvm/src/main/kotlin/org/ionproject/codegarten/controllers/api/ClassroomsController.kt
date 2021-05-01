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
import org.ionproject.codegarten.Routes.getUsersOfClassroomUri
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.controllers.api.actions.ClassroomActions.getCreateClassroomAction
import org.ionproject.codegarten.controllers.api.actions.ClassroomActions.getDeleteClassroomAction
import org.ionproject.codegarten.controllers.api.actions.ClassroomActions.getEditClassroomAction
import org.ionproject.codegarten.controllers.models.ClassroomCreateInputModel
import org.ionproject.codegarten.controllers.models.ClassroomEditInputModel
import org.ionproject.codegarten.controllers.models.ClassroomOutputModel
import org.ionproject.codegarten.controllers.models.ClassroomsOutputModel
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserClassroom
import org.ionproject.codegarten.database.dto.UserClassroomMembership
import org.ionproject.codegarten.database.helpers.ClassroomsDb
import org.ionproject.codegarten.database.helpers.UsersDb
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresGhAppInstallation
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInClassroom
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInOrg
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.responses.GitHubUserOrgRole
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
import org.springframework.web.bind.annotation.RestController

@RestController
class ClassroomsController(
    val classroomsDb: ClassroomsDb,
    val usersDb: UsersDb,
    val gitHub: GitHubInterface
) {

    @RequiresUserInOrg
    @GetMapping(CLASSROOMS_HREF)
    fun getUserClassrooms(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        pagination: Pagination,
        user: User
    ): ResponseEntity<Response> {
        val classrooms = classroomsDb.getClassroomsOfUser(orgId, user.uid, pagination.page, pagination.limit)
        val classroomsCount = classroomsDb.getClassroomsOfUserCount(orgId, user.uid)
        val org = gitHub.getOrgById(orgId, user.gh_token)

        return ClassroomsOutputModel(
            collectionSize = classroomsCount,
            pageIndex = pagination.page,
            pageSize = classrooms.size,
        ).toSirenObject(
            entities = classrooms.map {
                ClassroomOutputModel(
                    id = it.cid,
                    number = it.number,
                    name = it.name,
                    description = it.description,
                    organization = org.login
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM), getClassroomByNumberUri(orgId, it.number).includeHost()),
                        SirenLink(listOf("assignments"), getAssignmentsUri(orgId, it.number).includeHost()),
                        SirenLink(listOf("users"), getUsersOfClassroomUri(orgId, it.number).includeHost()),
                        SirenLink(listOf("classrooms"), getClassroomsUri(orgId).includeHost()),
                        SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()),
                    )
                )
            },
            actions = listOf(
                getCreateClassroomAction(orgId)
            ),
            links = createSirenLinkListForPagination(
                getClassroomsUri(orgId).includeHost(),
                pagination.page,
                pagination.limit,
                classroomsCount
            ) + listOf(SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()))
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserInClassroom
    @GetMapping(CLASSROOM_BY_NUMBER_HREF)
    fun getClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User,
        userClassroom: UserClassroom
    ): ResponseEntity<Response> {
        val classroom = userClassroom.classroom
        val org = gitHub.getOrgById(orgId, user.gh_token)

        return ClassroomOutputModel(
            id = classroom.cid,
            number = classroom.number,
            name = classroom.name,
            description = classroom.description,
            organization = org.login
        ).toSirenObject(
            actions = listOf(
                getEditClassroomAction(orgId, classroom.number),
                getDeleteClassroomAction(orgId, classroom.number)
            ),
            links = listOf(
                SirenLink(listOf(SELF_PARAM), getClassroomByNumberUri(orgId, classroom.number).includeHost()),
                SirenLink(listOf("assignments"), getAssignmentsUri(orgId, classroom.number).includeHost()),
                SirenLink(listOf("users"), getUsersOfClassroomUri(orgId, classroom.number).includeHost()),
                SirenLink(listOf("classrooms"), getClassroomsUri(orgId).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()),
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
        input: ClassroomCreateInputModel
    ): ResponseEntity<Response> {
        if (orgMembership != GitHubUserOrgRole.ADMIN) throw AuthorizationException("User is not an organization admin")

        if (input.name == null) throw InvalidInputException("Missing name")
        val createdClassroom = classroomsDb.createClassroom(orgId, input.name, input.description)
        usersDb.addUserToClassroom(createdClassroom.cid, user.uid, UserClassroomMembership.TEACHER)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location", getClassroomByNumberUri(orgId, createdClassroom.number).includeHost().toString())
            .body(null)
    }

    @RequiresUserInClassroom
    @PutMapping(CLASSROOM_BY_NUMBER_HREF)
    fun editClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User,
        userClassroom: UserClassroom,
        input: ClassroomEditInputModel
    ): ResponseEntity<Response> {
        if (userClassroom.role != UserClassroomMembership.TEACHER) throw AuthorizationException("User is not a classroom teacher")

        classroomsDb.editClassroom(orgId, classroomNumber, input.name, input.description)
        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Location", getClassroomByNumberUri(orgId, classroomNumber).includeHost().toString())
            .body(null)
    }

    @RequiresUserInClassroom
    @DeleteMapping(CLASSROOM_BY_NUMBER_HREF)
    fun deleteClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User,
        userClassroom: UserClassroom
    ): ResponseEntity<Response> {
        if (userClassroom.role != UserClassroomMembership.TEACHER) throw AuthorizationException("User is not a classroom teacher")

        classroomsDb.deleteClassroom(orgId, classroomNumber)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }
}