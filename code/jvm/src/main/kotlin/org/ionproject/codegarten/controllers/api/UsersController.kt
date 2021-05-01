package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.ASSIGNMENT_PARAM
import org.ionproject.codegarten.Routes.CLASSROOM_PARAM
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.Routes.SELF_PARAM
import org.ionproject.codegarten.Routes.USERS_OF_ASSIGNMENT_HREF
import org.ionproject.codegarten.Routes.USERS_OF_CLASSROOM_HREF
import org.ionproject.codegarten.Routes.USER_BY_ID_HREF
import org.ionproject.codegarten.Routes.USER_HREF
import org.ionproject.codegarten.Routes.USER_OF_ASSIGNMENT_HREF
import org.ionproject.codegarten.Routes.USER_OF_CLASSROOM_HREF
import org.ionproject.codegarten.Routes.USER_PARAM
import org.ionproject.codegarten.Routes.getUserByIdUri
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.controllers.api.actions.UserActions
import org.ionproject.codegarten.controllers.models.UserAddInputModel
import org.ionproject.codegarten.controllers.models.UserEditInputModel
import org.ionproject.codegarten.controllers.models.UserOutputModel
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.helpers.UsersDb
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserAuth
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.GitHubRoutes
import org.ionproject.codegarten.responses.Response
import org.ionproject.codegarten.responses.siren.SirenLink
import org.ionproject.codegarten.responses.toResponseEntity
import org.ionproject.codegarten.utils.CryptoUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class UsersController(
    val usersDb: UsersDb,
    val gitHub: GitHubInterface,
    val cryptoUtils: CryptoUtils,
) {

    @RequiresUserAuth
    @GetMapping(USER_HREF)
    fun getAuthenticatedUser(
        user: User
    ): ResponseEntity<Response> {
        val ghUser = gitHub.getUserInfo(user.gh_token)

        return UserOutputModel(
            id = user.uid,
            name = user.name,
            gitHubId = user.gh_id,
            gitHubName = ghUser.login
        ).toSirenObject(
            actions = listOf(
                UserActions.getEditUserAction(),
                UserActions.getDeleteUserAction()
            ),
            links = listOf(
                SirenLink(listOf(SELF_PARAM), URI(USER_HREF).includeHost()),
                SirenLink(listOf("github"), GitHubRoutes.getGithubLoginUri(ghUser.login)),
                SirenLink(listOf("avatar"), URI(ghUser.avatar_url))
            )
        ).toResponseEntity(HttpStatus.OK)
    }

    @GetMapping(USER_BY_ID_HREF)
    fun getUserById(
        @PathVariable(name = USER_PARAM) userId: Int,
    ): ResponseEntity<Response> {
        val user = usersDb.getUserById(userId)
        val ghUser = gitHub.getUser(user.gh_id, cryptoUtils.decrypt(user.gh_token))

        return UserOutputModel(
            id = user.uid,
            name = user.name,
            gitHubId = user.gh_id,
            gitHubName = ghUser.login
        ).toSirenObject(
            links = listOf(
                SirenLink(listOf(SELF_PARAM), getUserByIdUri(userId).includeHost()),
                SirenLink(listOf("github"), GitHubRoutes.getGithubLoginUri(ghUser.login)),
                SirenLink(listOf("avatar"), URI(ghUser.avatar_url))
            )
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserAuth
    @PutMapping(USER_HREF)
    fun editUser(
        user: User,
        input: UserEditInputModel
    ): ResponseEntity<Any> {
        if (input.name == null) throw InvalidInputException("Missing name")

        usersDb.editUser(user.uid, input.name)
        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Location", getUserByIdUri(user.uid).includeHost().toString())
            .body(null)
    }

    @RequiresUserAuth
    @DeleteMapping(USER_HREF)
    fun deleteUser(
        user: User,
    ): ResponseEntity<Any> {
        usersDb.deleteUser(user.uid)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }

    // Users of Classrooms Handlers
    @RequiresUserAuth
    @GetMapping(USERS_OF_CLASSROOM_HREF)
    fun getUsersOfClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        pagination: Pagination,
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PutMapping(USERS_OF_CLASSROOM_HREF)
    fun addUserToClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User,
        input: UserAddInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @DeleteMapping(USER_OF_CLASSROOM_HREF)
    fun removeUserFromClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = USER_PARAM) userId: Int,
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }

    // Users of Assignments Handlers
    @RequiresUserAuth
    @GetMapping(USERS_OF_ASSIGNMENT_HREF)
    fun getUsersOfAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        pagination: Pagination,
        user: User
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @PutMapping(USERS_OF_ASSIGNMENT_HREF)
    fun addUserToAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        user: User,
        input: UserAddInputModel
    ): ResponseEntity<Response> {
        TODO()
    }

    @RequiresUserAuth
    @DeleteMapping(USER_OF_ASSIGNMENT_HREF)
    fun removeUserFromAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = USER_PARAM) userId: Int,
        user: User,
    ): ResponseEntity<Response> {
        TODO()
    }
}