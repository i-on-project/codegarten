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
import org.ionproject.codegarten.Routes.createSirenLinkListForPagination
import org.ionproject.codegarten.Routes.getAssignmentByNumberUri
import org.ionproject.codegarten.Routes.getClassroomByNumberUri
import org.ionproject.codegarten.Routes.getDeliveriesOfUserUri
import org.ionproject.codegarten.Routes.getOrgByIdUri
import org.ionproject.codegarten.Routes.getUserByIdUri
import org.ionproject.codegarten.Routes.getUsersOfClassroomUri
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.controllers.api.actions.UserActions
import org.ionproject.codegarten.controllers.models.UserAddInputModel
import org.ionproject.codegarten.controllers.models.UserAssignmentOutputModel
import org.ionproject.codegarten.controllers.models.UserClassroomOutputModel
import org.ionproject.codegarten.controllers.models.UserEditInputModel
import org.ionproject.codegarten.controllers.models.UserOutputModel
import org.ionproject.codegarten.controllers.models.UsersOutputModel
import org.ionproject.codegarten.controllers.models.validRoleTypes
import org.ionproject.codegarten.database.dto.Assignment
import org.ionproject.codegarten.database.dto.Installation
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserClassroom
import org.ionproject.codegarten.database.dto.UserClassroomMembership.NOT_A_MEMBER
import org.ionproject.codegarten.database.dto.UserClassroomMembership.TEACHER
import org.ionproject.codegarten.database.helpers.UsersDb
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.ionproject.codegarten.exceptions.HttpRequestException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresGhAppInstallation
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserAuth
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInAssignment
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInClassroom
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.GitHubRoutes
import org.ionproject.codegarten.remote.github.GitHubRoutes.generateCodeGartenRepoName
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubAvatarUri
import org.ionproject.codegarten.remote.github.responses.GitHubRepoResponse
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
import org.springframework.web.bind.annotation.RequestBody
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
        @RequestBody input: UserEditInputModel?
    ): ResponseEntity<Any> {
        if (input == null) throw InvalidInputException("Missing body")
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

    @RequiresUserInClassroom
    @GetMapping(USERS_OF_CLASSROOM_HREF)
    fun getUsersOfClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        pagination: Pagination,
        user: User,
        userClassroom: UserClassroom
    ): ResponseEntity<Response> {
        val users = usersDb.getUsersInClassroom(orgId, classroomNumber, pagination.page, pagination.limit)
        val usersCount = usersDb.getUsersInClassroomCount(orgId, classroomNumber)

        val actions =
            if (userClassroom.role == TEACHER)
                listOf(
                    UserActions.getAddUserToClassroom(orgId, classroomNumber),
                    UserActions.getRemoveUserFromClassroom(orgId, classroomNumber)
                )
            else
                null

        return UsersOutputModel(
            collectionSize = usersCount,
            pageIndex = pagination.page,
            pageSize = users.size
        ).toSirenObject(
            entities = users.map {
                UserClassroomOutputModel(
                    id = it.uid,
                    name = it.name,
                    gitHubId = it.gh_id,
                    role = it.classroom_role
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM), getUserByIdUri(it.uid).includeHost()),
                        SirenLink(listOf("avatar"), getGitHubAvatarUri(it.gh_id)),
                        SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                        SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()))
                    )
            },
            actions = actions,
            links = createSirenLinkListForPagination(
                getUsersOfClassroomUri(orgId, classroomNumber).includeHost(),
                pagination.page,
                pagination.limit,
                usersCount
            ) + listOf(
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()))
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserInClassroom
    @PutMapping(USER_OF_CLASSROOM_HREF)
    fun addUserToClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = USER_PARAM) userId: Int,
        user: User,
        userClassroom: UserClassroom,
        @RequestBody input: UserAddInputModel?
    ): ResponseEntity<Any> {
        if (userClassroom.role != TEACHER) throw AuthorizationException("User is not a teacher")

        // User cannot add itself into the classroom, so it's safe to assume it's an edit request
        if (user.uid == userId) throw InvalidInputException("Cannot edit user with id '$userId' while authenticated as itself")

        if (input == null) throw InvalidInputException("Missing body")
        if (input.role == null) throw InvalidInputException("Missing role")
        if (!validRoleTypes.contains(input.role)) throw InvalidInputException("Invalid role. Must be one of: $validRoleTypes")

        usersDb.addOrEditUserInClassroom(orgId, classroomNumber, userId, input.role)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(null)
    }

    @RequiresUserInClassroom
    @DeleteMapping(USER_OF_CLASSROOM_HREF)
    fun removeUserFromClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = USER_PARAM) userId: Int,
        user: User,
        userClassroom: UserClassroom,
    ): ResponseEntity<Any> {
        if (userClassroom.role != TEACHER) throw AuthorizationException("User is not a teacher")

        usersDb.deleteUserFromClassroom(orgId, classroomNumber, userId)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }

    // Users of Assignments Handlers

    // TODO: Check when Team
    @RequiresUserInAssignment
    @GetMapping(USERS_OF_ASSIGNMENT_HREF)
    fun getUsersOfAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        pagination: Pagination,
        user: User,
        userClassroom: UserClassroom,
        assignment: Assignment
    ): ResponseEntity<Response> {
        val users = usersDb.getUsersInAssignment(orgId, classroomNumber, assignmentNumber, pagination.page, pagination.limit)
        val usersCount = usersDb.getUsersInAssignmentCount(orgId, classroomNumber, assignmentNumber)

        val actions =
            if (userClassroom.role == TEACHER)
                listOf(
                    UserActions.getAddUserToAssignment(orgId, classroomNumber, assignmentNumber, false),
                    UserActions.getRemoveUserFromAssignment(orgId, classroomNumber, assignmentNumber)
                )
            else
                null

        return UsersOutputModel(
            collectionSize = usersCount,
            pageIndex = pagination.page,
            pageSize = users.size
        ).toSirenObject(
            entities = users.map {
                UserAssignmentOutputModel(
                    id = it.uid,
                    name = it.name,
                    gitHubId = it.gh_id,
                    repoId = it.repo_id
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOfNotNull(
                        SirenLink(listOf(SELF_PARAM), getUserByIdUri(it.uid).includeHost()),
                        SirenLink(listOf("avatar"), getGitHubAvatarUri(it.gh_id)),
                        if (userClassroom.role == TEACHER || it.uid == user.uid)
                            SirenLink(listOf("deliveries"), getDeliveriesOfUserUri(orgId, classroomNumber, assignmentNumber, it.uid).includeHost())
                        else
                            null,
                        SirenLink(listOf("assignment"), getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                        SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                        SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()))
                )
            },
            actions = actions,
            links = createSirenLinkListForPagination(
                getUsersOfClassroomUri(orgId, classroomNumber).includeHost(),
                pagination.page,
                pagination.limit,
                usersCount
            ) + listOf(
                SirenLink(listOf("assignment"), getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()))
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresGhAppInstallation
    @RequiresUserInAssignment
    @PutMapping(USER_OF_ASSIGNMENT_HREF)
    fun addUserToAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = USER_PARAM) userId: Int,
        user: User,
        userClassroom: UserClassroom,
        assignment: Assignment,
        installation: Installation
    ): ResponseEntity<Response> {
        if (userClassroom.role != TEACHER) throw AuthorizationException("User is not a teacher")

        val userMembershipInClassroom = usersDb.getUserMembershipInClassroom(orgId, classroomNumber, userId)

        if (userMembershipInClassroom.role == NOT_A_MEMBER) throw InvalidInputException("User is not in the classroom")
        if (userMembershipInClassroom.role == TEACHER) throw InvalidInputException("Cannot add a teacher to an assignment")
        val userToAdd = userMembershipInClassroom.user!!
        val org = gitHub.getOrgById(orgId, user.gh_token)

        val repoName =
            generateCodeGartenRepoName(userClassroom.classroom.number, assignment.repo_prefix, userToAdd.name)

        val repo: GitHubRepoResponse
        try {
            repo =
                if (assignment.repo_template == null) gitHub.createRepo(orgId, repoName, installation.accessToken)
                else gitHub.createRepoFromTemplate(org.login, repoName, assignment.repo_template, installation.accessToken)

            val ghUser = gitHub.getUser(userToAdd.gh_id, user.gh_token)
            gitHub.addUserToRepo(repo.id, ghUser.login, installation.accessToken)
        } catch (ex: HttpRequestException) {
            if (ex.status == HttpStatus.UNPROCESSABLE_ENTITY.value())
                throw InvalidInputException("User is already in assignment")
            else throw ex
        }

        usersDb.addUserToAssignment(orgId, classroomNumber, assignmentNumber, userId, repo.id)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location", repo.html_url)
            .body(null)
    }

    @RequiresUserInAssignment
    @DeleteMapping(USER_OF_ASSIGNMENT_HREF)
    fun removeUserFromAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = USER_PARAM) userId: Int,
        user: User,
        userClassroom: UserClassroom,
    ): ResponseEntity<Response> {
        if (userClassroom.role != TEACHER) throw AuthorizationException("User is not a teacher")

        usersDb.deleteUserFromAssignment(orgId, classroomNumber, assignmentNumber, userId)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }
}