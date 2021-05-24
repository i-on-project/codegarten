package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.CLASSROOM_PARAM
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.Routes.SELF_PARAM
import org.ionproject.codegarten.Routes.TEAM_PARAM
import org.ionproject.codegarten.Routes.USERS_OF_CLASSROOM_HREF
import org.ionproject.codegarten.Routes.USERS_OF_TEAM_HREF
import org.ionproject.codegarten.Routes.USER_BY_ID_HREF
import org.ionproject.codegarten.Routes.USER_HREF
import org.ionproject.codegarten.Routes.USER_OF_CLASSROOM_HREF
import org.ionproject.codegarten.Routes.USER_OF_TEAM_HREF
import org.ionproject.codegarten.Routes.USER_PARAM
import org.ionproject.codegarten.Routes.createSirenLinkListForPagination
import org.ionproject.codegarten.Routes.getClassroomByNumberUri
import org.ionproject.codegarten.Routes.getOrgByIdUri
import org.ionproject.codegarten.Routes.getTeamByNumberUri
import org.ionproject.codegarten.Routes.getUserByIdUri
import org.ionproject.codegarten.Routes.getUsersOfClassroomUri
import org.ionproject.codegarten.Routes.getUsersOfTeamUri
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.controllers.api.actions.UserActions
import org.ionproject.codegarten.controllers.models.UserAddInputModel
import org.ionproject.codegarten.controllers.models.UserClassroomOutputModel
import org.ionproject.codegarten.controllers.models.UserEditInputModel
import org.ionproject.codegarten.controllers.models.UserItemOutputModel
import org.ionproject.codegarten.controllers.models.UserOutputModel
import org.ionproject.codegarten.controllers.models.UsersOutputModel
import org.ionproject.codegarten.controllers.models.validRoleTypes
import org.ionproject.codegarten.database.dto.Installation
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserClassroom
import org.ionproject.codegarten.database.dto.UserClassroomMembership.NOT_A_MEMBER
import org.ionproject.codegarten.database.dto.UserClassroomMembership.TEACHER
import org.ionproject.codegarten.database.helpers.TeamsDb
import org.ionproject.codegarten.database.helpers.UsersDb
import org.ionproject.codegarten.exceptions.ForbiddenException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresGhAppInstallation
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserAuth
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInClassroom
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.GitHubRoutes
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubUserAvatarUri
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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class UsersController(
    val usersDb: UsersDb,
    val teamsDb: TeamsDb,
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

    @RequiresUserAuth
    @GetMapping(USER_BY_ID_HREF)
    fun getUserById(
        @PathVariable(name = USER_PARAM) userId: Int,
        user: User,
    ): ResponseEntity<Response> {
        val requestedUser = usersDb.getUserById(userId)
        val requestedGitHubUser = gitHub.getUser(requestedUser.gh_id, user.gh_token)

        return UserOutputModel(
            id = requestedUser.uid,
            name = requestedUser.name,
            gitHubName = requestedGitHubUser.login
        ).toSirenObject(
            links = listOf(
                SirenLink(listOf(SELF_PARAM), getUserByIdUri(userId).includeHost()),
                SirenLink(listOf("github"), GitHubRoutes.getGithubLoginUri(requestedGitHubUser.login)),
                SirenLink(listOf("avatar"), URI(requestedGitHubUser.avatar_url))
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
                    role = it.classroom_role
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM), getUserByIdUri(it.uid).includeHost()),
                        SirenLink(listOf("avatar"), getGitHubUserAvatarUri(it.gh_id)),
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

    @RequiresGhAppInstallation
    @RequiresUserInClassroom
    @PutMapping(USER_OF_CLASSROOM_HREF)
    fun addUserToClassroom(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = USER_PARAM) userId: Int,
        installation: Installation,
        user: User,
        userClassroom: UserClassroom,
        @RequestBody input: UserAddInputModel?
    ): ResponseEntity<Any> {
        if (userClassroom.role != TEACHER) throw ForbiddenException("User is not a teacher")

        // User cannot add itself into the classroom, so it's safe to assume it's an edit request
        if (user.uid == userId) throw ForbiddenException("Cannot edit user with id '$userId' while authenticated as itself")

        if (input == null) throw InvalidInputException("Missing body")
        if (input.role == null) throw InvalidInputException("Missing role")
        if (!validRoleTypes.contains(input.role)) throw InvalidInputException("Invalid role. Must be one of: $validRoleTypes")

        val userToAdd = usersDb.getUserById(userId)
        val classroomMembership = usersDb.getUserMembershipInClassroom(userClassroom.classroom.cid, userId)

        val status =
            if (classroomMembership.role == NOT_A_MEMBER) {
                if (gitHub.getUserOrgMembership(orgId, cryptoUtils.decrypt(userToAdd.gh_token)).role == GitHubUserOrgRole.NOT_A_MEMBER) {
                    gitHub.inviteUserToOrg(orgId, userToAdd.gh_id, installationToken = installation.accessToken)
                }
                HttpStatus.CREATED
            } else {
                HttpStatus.OK
            }

        usersDb.addOrEditUserInClassroom(orgId, classroomNumber, userId, input.role)

        return ResponseEntity
            .status(status)
            .header("Location", getClassroomByNumberUri(orgId, classroomNumber).includeHost().toString())
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
        if (userClassroom.role != TEACHER && user.uid != userId)
            throw ForbiddenException("Not enough permissions to remove another user")

        // Don't allow teachers to leave if they are the only teacher in the classroom
        val isTeacherLeaving = userClassroom.role == TEACHER && user.uid == userId
        if (isTeacherLeaving && usersDb.getTeachersInClassroomCount(userClassroom.classroom.cid) == 1)
            throw ForbiddenException("Unable to leave classroom as there are no other teachers")


        usersDb.deleteUserFromClassroom(orgId, classroomNumber, userId)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }

    @RequiresUserInClassroom
    @GetMapping(USERS_OF_TEAM_HREF)
    fun getUsersOfTeam(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = TEAM_PARAM) teamNumber: Int,
        pagination: Pagination,
        user: User,
        userClassroom: UserClassroom
    ): ResponseEntity<Response> {
        val team = teamsDb.getTeam(orgId, classroomNumber, teamNumber)
        if (userClassroom.role != TEACHER && !teamsDb.isUserInTeam(user.uid, team.tid))
            throw ForbiddenException("User is not in team")

        val users = usersDb.getUsersInTeam(team.tid, pagination.page, pagination.limit)
        val usersCount = usersDb.getUsersInTeamCount(team.tid)

        val actions =
            if (userClassroom.role == TEACHER)
                listOf(
                    UserActions.getAddUserToTeam(orgId, classroomNumber, teamNumber),
                    UserActions.getRemoveUserFromTeam(orgId, classroomNumber, teamNumber)
                )
            else
                null

        return UsersOutputModel(
            collectionSize = usersCount,
            pageIndex = pagination.page,
            pageSize = users.size
        ).toSirenObject(
            entities = users.map {
                UserItemOutputModel(
                    id = it.uid,
                    name = it.name
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOfNotNull(
                        SirenLink(listOf(SELF_PARAM), getUserByIdUri(it.uid).includeHost()),
                        SirenLink(listOf("avatar"), getGitHubUserAvatarUri(it.gh_id)),
                        SirenLink(listOf("team"), getTeamByNumberUri(orgId, classroomNumber, teamNumber).includeHost()),
                        SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                        SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()))
                )
            },
            actions = actions,
            links = createSirenLinkListForPagination(
                getUsersOfTeamUri(orgId, classroomNumber, teamNumber).includeHost(),
                pagination.page,
                pagination.limit,
                usersCount
            ) + listOf(
                SirenLink(listOf("team"), getTeamByNumberUri(orgId, classroomNumber, teamNumber).includeHost()),
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()))
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresGhAppInstallation
    @RequiresUserInClassroom
    @PutMapping(USER_OF_TEAM_HREF)
    fun addUserToTeam(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = TEAM_PARAM) teamNumber: Int,
        @PathVariable(name = USER_PARAM) userId: Int,
        installation: Installation,
        user: User,
        userClassroom: UserClassroom
    ): ResponseEntity<Any> {
        if (userClassroom.role != TEACHER) throw ForbiddenException("User is not a teacher")

        val team = teamsDb.getTeam(orgId, classroomNumber, teamNumber)
        val userToAdd = usersDb.getUserById(userId)

        val githubUser = gitHub.getUser(userToAdd.gh_id, user.gh_token)

        gitHub.addUserToTeam(orgId, team.gh_id, githubUser.login, installation.accessToken)
        usersDb.addUserToTeam(team.tid, userId)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(null)
    }

    @RequiresGhAppInstallation
    @RequiresUserInClassroom
    @DeleteMapping(USER_OF_TEAM_HREF)
    fun removeUserFromTeam(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = TEAM_PARAM) teamNumber: Int,
        @PathVariable(name = USER_PARAM) userId: Int,
        installation: Installation,
        user: User,
        userClassroom: UserClassroom,
    ): ResponseEntity<Any> {
        if (userClassroom.role != TEACHER && user.uid != userId)
            throw ForbiddenException("Not enough permissions to remove another user")

        val team = teamsDb.getTeam(orgId, classroomNumber, teamNumber)
        val userToRemove = usersDb.getUserById(userId)

        val githubUser = gitHub.getUser(userToRemove.gh_id, user.gh_token)

        gitHub.removeUserFromTeam(orgId, team.gh_id, githubUser.login, installation.accessToken)
        usersDb.deleteUserFromTeam(team.tid, userId)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }
}