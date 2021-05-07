package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.ASSIGNMENT_PARAM
import org.ionproject.codegarten.Routes.CLASSROOM_PARAM
import org.ionproject.codegarten.Routes.INVITE_CODE_PARAM
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.Routes.SELF_PARAM
import org.ionproject.codegarten.Routes.TEAM_PARAM
import org.ionproject.codegarten.Routes.PARTICIPANTS_OF_ASSIGNMENT_HREF
import org.ionproject.codegarten.Routes.USERS_OF_CLASSROOM_HREF
import org.ionproject.codegarten.Routes.USERS_OF_TEAM_HREF
import org.ionproject.codegarten.Routes.USER_BY_ID_HREF
import org.ionproject.codegarten.Routes.USER_HREF
import org.ionproject.codegarten.Routes.PARTICIPANT_OF_ASSIGNMENT_HREF
import org.ionproject.codegarten.Routes.PARTICIPANT_PARAM
import org.ionproject.codegarten.Routes.USER_CLASSROOM_HREF
import org.ionproject.codegarten.Routes.USER_OF_CLASSROOM_HREF
import org.ionproject.codegarten.Routes.USER_OF_TEAM_HREF
import org.ionproject.codegarten.Routes.USER_PARAM
import org.ionproject.codegarten.Routes.createSirenLinkListForPagination
import org.ionproject.codegarten.Routes.getAssignmentByNumberUri
import org.ionproject.codegarten.Routes.getClassroomByNumberUri
import org.ionproject.codegarten.Routes.getDeliveriesOfParticipantUri
import org.ionproject.codegarten.Routes.getOrgByIdUri
import org.ionproject.codegarten.Routes.getParticipantsOfAssignmentUri
import org.ionproject.codegarten.Routes.getTeamByNumberUri
import org.ionproject.codegarten.Routes.getUserByIdUri
import org.ionproject.codegarten.Routes.getUsersOfClassroomUri
import org.ionproject.codegarten.Routes.getUsersOfTeamUri
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.controllers.api.actions.UserActions
import org.ionproject.codegarten.controllers.models.OutputModel
import org.ionproject.codegarten.controllers.models.UserAddInputModel
import org.ionproject.codegarten.controllers.models.ParticipantOutputModel
import org.ionproject.codegarten.controllers.models.ParticipantTypes
import org.ionproject.codegarten.controllers.models.ParticipantsOutputModel
import org.ionproject.codegarten.controllers.models.UserClassroomOutputModel
import org.ionproject.codegarten.controllers.models.UserEditInputModel
import org.ionproject.codegarten.controllers.models.UserItemOutputModel
import org.ionproject.codegarten.controllers.models.UserOutputModel
import org.ionproject.codegarten.controllers.models.UsersOutputModel
import org.ionproject.codegarten.controllers.models.validRoleTypes
import org.ionproject.codegarten.database.dto.Assignment
import org.ionproject.codegarten.database.dto.Installation
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserClassroom
import org.ionproject.codegarten.database.dto.UserClassroomMembership.NOT_A_MEMBER
import org.ionproject.codegarten.database.dto.UserClassroomMembership.TEACHER
import org.ionproject.codegarten.database.dto.isGroupAssignment
import org.ionproject.codegarten.database.dto.isIndividualAssignment
import org.ionproject.codegarten.database.helpers.AssignmentsDb
import org.ionproject.codegarten.database.helpers.ClassroomsDb
import org.ionproject.codegarten.database.helpers.TeamsDb
import org.ionproject.codegarten.database.helpers.UsersDb
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.ionproject.codegarten.exceptions.HttpRequestException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.exceptions.NotFoundException
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresGhAppInstallation
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserAuth
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInAssignment
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInClassroom
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.GitHubRoutes
import org.ionproject.codegarten.remote.github.GitHubRoutes.generateCodeGartenRepoName
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubTeamAvatarUri
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubUserAvatarUri
import org.ionproject.codegarten.remote.github.responses.GitHubRepoResponse
import org.ionproject.codegarten.remote.github.responses.GitHubUserOrgRole
import org.ionproject.codegarten.responses.Response
import org.ionproject.codegarten.responses.siren.Siren
import org.ionproject.codegarten.responses.siren.SirenAction
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
    val classroomsDb: ClassroomsDb,
    val assignmentsDb: AssignmentsDb,
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

    @GetMapping(USER_BY_ID_HREF)
    fun getUserById(
        @PathVariable(name = USER_PARAM) userId: Int,
    ): ResponseEntity<Response> {
        val user = usersDb.getUserById(userId)
        val ghUser = gitHub.getUser(user.gh_id, cryptoUtils.decrypt(user.gh_token))

        return UserOutputModel(
            id = user.uid,
            name = user.name,
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
        if (userClassroom.role != TEACHER) throw AuthorizationException("User is not a teacher")

        // User cannot add itself into the classroom, so it's safe to assume it's an edit request
        if (user.uid == userId) throw InvalidInputException("Cannot edit user with id '$userId' while authenticated as itself")

        if (input == null) throw InvalidInputException("Missing body")
        if (input.role == null) throw InvalidInputException("Missing role")
        if (!validRoleTypes.contains(input.role)) throw InvalidInputException("Invalid role. Must be one of: $validRoleTypes")

        val userToAdd = usersDb.getUserById(userId)

        if (gitHub.getUserOrgMembership(orgId, userToAdd.gh_token).role == GitHubUserOrgRole.NOT_A_MEMBER) {
            gitHub.inviteUserToOrg(orgId, userToAdd.gh_id, installation.accessToken)
        }

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

    // Participants of Assignments Handlers

    @RequiresUserInAssignment
    @GetMapping(PARTICIPANTS_OF_ASSIGNMENT_HREF)
    fun getParticipantsInAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        pagination: Pagination,
        user: User,
        userClassroom: UserClassroom,
        assignment: Assignment
    ): ResponseEntity<Response> {
        //TODO: Add Repo Links to participants
        val isGroupAssignment = assignment.isGroupAssignment()
        val isTeacher = userClassroom.role == TEACHER

        val actions =
            if (isTeacher)
                listOf(
                    UserActions.getAddParticipantToAssignment(orgId, classroomNumber, assignmentNumber, isGroupAssignment),
                    UserActions.getRemoveParticipantFromAssignment(orgId, classroomNumber, assignmentNumber, isGroupAssignment)
                )
            else null

        val participantsSirenObject =
            if (isGroupAssignment)
                getTeamsInAssignment(
                    orgId, classroomNumber, assignmentNumber,
                    assignment.aid, isTeacher, user.uid,
                    actions, pagination
                )
            else
                getUsersInAssignment(
                    orgId, classroomNumber, assignmentNumber,
                    isTeacher, user.uid, actions, pagination
                )

        return participantsSirenObject.toResponseEntity(HttpStatus.OK)
    }

    private fun getUsersInAssignment(
        orgId: Int, classroomNumber: Int, assignmentNumber: Int,
        isTeacher: Boolean, userId: Int, actions: List<SirenAction>?,
        pagination: Pagination
    ): Siren<OutputModel> {
        val users = usersDb.getUsersInAssignment(orgId, classroomNumber, assignmentNumber, pagination.page, pagination.limit)
        val usersCount = usersDb.getUsersInAssignmentCount(orgId, classroomNumber, assignmentNumber)

        return ParticipantsOutputModel(
            participantsType = ParticipantTypes.USER.type,
            collectionSize = usersCount,
            pageIndex = pagination.page,
            pageSize = users.size
        ).toSirenObject(
            entities = users.map {
                ParticipantOutputModel(
                    id = it.uid,
                    name = it.name
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOfNotNull(
                        SirenLink(listOf(SELF_PARAM), getUserByIdUri(it.uid).includeHost()),
                        SirenLink(listOf("avatar"), getGitHubUserAvatarUri(it.gh_id)),
                        if (isTeacher || it.uid == userId)
                            SirenLink(listOf("deliveries"), getDeliveriesOfParticipantUri(orgId, classroomNumber, assignmentNumber, it.uid).includeHost())
                        else
                            null,
                        SirenLink(listOf("assignment"), getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                        SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                        SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()))
                )
            },
            actions = actions,
            links = createSirenLinkListForPagination(
                getParticipantsOfAssignmentUri(orgId, classroomNumber, assignmentNumber).includeHost(),
                pagination.page,
                pagination.limit,
                usersCount
            ) + listOf(
                SirenLink(listOf("assignment"), getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()))
        )
    }

    private fun getTeamsInAssignment(
        orgId: Int, classroomNumber: Int, assignmentNumber: Int,
        assignmentId: Int, isTeacher: Boolean, userId: Int,
        actions: List<SirenAction>?, pagination: Pagination
    ): Siren<OutputModel> {
        val teams = teamsDb.getTeamsFromAssignment(assignmentId, pagination.page, pagination.limit)
        val teamsCount = teamsDb.getTeamsFromAssignmentCount(assignmentId)

        // TODO: Find a better way to deal with deliveries URI
        return ParticipantsOutputModel(
            participantsType = ParticipantTypes.TEAM.type,
            collectionSize = teamsCount,
            pageIndex = pagination.page,
            pageSize = teams.size
        ).toSirenObject(
            entities = teams.map {
                ParticipantOutputModel(
                    id = it.tid,
                    name = it.name
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOfNotNull(
                        SirenLink(listOf(SELF_PARAM), getTeamByNumberUri(orgId, classroomNumber, it.number).includeHost()),
                        SirenLink(listOf("avatar"), getGitHubTeamAvatarUri(it.gh_id)),
                        if (isTeacher)
                            SirenLink(listOf("deliveries"), getDeliveriesOfParticipantUri(orgId, classroomNumber, assignmentNumber, it.number).includeHost())
                        else
                            null,
                        SirenLink(listOf("assignment"), getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                        SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                        SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()))
                )
            },
            actions = actions,
            links = createSirenLinkListForPagination(
                getParticipantsOfAssignmentUri(orgId, classroomNumber, assignmentNumber).includeHost(),
                pagination.page,
                pagination.limit,
                teamsCount
            ) + listOf(
                SirenLink(listOf("assignment"), getAssignmentByNumberUri(orgId, classroomNumber, assignmentNumber).includeHost()),
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()))
        )
    }

    @RequiresGhAppInstallation
    @RequiresUserInAssignment
    @PutMapping(PARTICIPANT_OF_ASSIGNMENT_HREF)
    fun addParticipantToAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = PARTICIPANT_PARAM) participantId: Int,
        user: User,
        userClassroom: UserClassroom,
        assignment: Assignment,
        installation: Installation
    ): ResponseEntity<Any> {
        if (userClassroom.role != TEACHER) throw AuthorizationException("User is not a teacher")
        val repo =
            if (assignment.isIndividualAssignment()) {
                addUserToAssignment(orgId, assignment, participantId, installation.accessToken, user.gh_token)
            } else {
                addTeamToAssignment(orgId, assignment, participantId, installation.accessToken, user.gh_token)
            }

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location", repo.html_url)
            .body(null)
    }

    private fun addUserToAssignment(
        orgId: Int, assignment: Assignment, userId: Int,
        installationToken: String, userGhToken: String
    ): GitHubRepoResponse {
        val userMembershipInClassroom = usersDb.getUserMembershipInClassroom(orgId, assignment.classroom_number, userId)

        if (userMembershipInClassroom.role == NOT_A_MEMBER) throw InvalidInputException("User is not in the classroom")
        if (userMembershipInClassroom.role == TEACHER) throw InvalidInputException("Cannot add a teacher to an assignment")
        val userToAdd = userMembershipInClassroom.user!!

        val org = gitHub.getOrgById(orgId, userGhToken)

        val repoName =
            generateCodeGartenRepoName(assignment.classroom_number, assignment.repo_prefix, userToAdd.name)

        try {
            val repo =
                if (assignment.repo_template == null) gitHub.createRepo(orgId, repoName, installationToken)
                else gitHub.createRepoFromTemplate(org.login, repoName, assignment.repo_template, installationToken)

            val ghUser = gitHub.getUser(userToAdd.gh_id, userGhToken)
            gitHub.addUserToRepo(repo.id, ghUser.login, installationToken)

            usersDb.addUserToAssignment(orgId, assignment.classroom_number, assignment.number, userId, repo.id)
            return repo
        } catch (ex: HttpRequestException) {
            if (ex.status == HttpStatus.UNPROCESSABLE_ENTITY.value())
                throw InvalidInputException("User is already in assignment")
            else throw ex
        }
    }

    private fun addTeamToAssignment(
        orgId: Int, assignment: Assignment, teamNumber: Int,
        installationToken: String, userGhToken: String
    ): GitHubRepoResponse {
        val team = teamsDb.getTeam(assignment.classroom_id, teamNumber)
        val org = gitHub.getOrgById(orgId, userGhToken)
        val repoName =
            generateCodeGartenRepoName(assignment.classroom_number, assignment.repo_prefix, team.name)

        try {
            val repo =
                if (assignment.repo_template == null) gitHub.createRepo(orgId, repoName, installationToken)
                else gitHub.createRepoFromTemplate(org.login, repoName, assignment.repo_template, installationToken)

            gitHub.addTeamToRepo(org.id, org.login, repo.name, team.gh_id, installationToken)
            teamsDb.addTeamToAssignment(assignment.aid, team.tid, repo.id)
            return repo
        } catch (ex: HttpRequestException) {
            if (ex.status == HttpStatus.UNPROCESSABLE_ENTITY.value())
                throw InvalidInputException("Team is already in assignment")
            else throw ex
        }
    }

    @RequiresUserInAssignment
    @DeleteMapping(PARTICIPANT_OF_ASSIGNMENT_HREF)
    fun removeParticipantFromAssignment(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = ASSIGNMENT_PARAM) assignmentNumber: Int,
        @PathVariable(name = PARTICIPANT_PARAM) participantId: Int,
        assignment: Assignment,
        user: User,
        userClassroom: UserClassroom
    ): ResponseEntity<Any> {
        if (userClassroom.role != TEACHER) throw AuthorizationException("User is not a teacher")

        if (assignment.isIndividualAssignment()) usersDb.deleteUserFromAssignment(orgId, classroomNumber, assignmentNumber, participantId)
        else teamsDb.deleteTeamFromAssignment(orgId, classroomNumber, assignmentNumber, participantId)

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
            throw AuthorizationException("User is not in team")

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
        if (userClassroom.role != TEACHER) throw AuthorizationException("User is not a teacher")

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
        if (userClassroom.role != TEACHER && user.uid != userId) throw AuthorizationException("Not enough permissions to remove another user")

        val team = teamsDb.getTeam(orgId, classroomNumber, teamNumber)
        val userToRemove = usersDb.getUserById(userId)

        val githubUser = gitHub.getUser(userToRemove.gh_id, user.gh_token)

        gitHub.removeUserFromTeam(orgId, team.gh_id, githubUser.login, installation.accessToken)
        usersDb.deleteUserFromTeam(team.tid, userId)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }

    @RequiresUserAuth
    @PutMapping(USER_CLASSROOM_HREF)
    fun addAuthUserToClassroom(
        @PathVariable(name = INVITE_CODE_PARAM) inviteCode: String,
        user: User
    ): ResponseEntity<Any> {
        // TODO: Need a way to get Installation token
        val maybeClassroom = classroomsDb.tryGetClassroomByInviteCode(inviteCode)
        if (maybeClassroom.isEmpty) throw NotFoundException("Invite code does not exist")
        val classroom = maybeClassroom.get()

        /*
        if (gitHub.getUserOrgMembership(classroom.org_id, user.gh_token).role == GitHubUserOrgRole.NOT_A_MEMBER) {
            gitHub.inviteUserToOrg(classroom.org_id, user.gh_id, installation.accessToken)
        }
        */

        // TODO: Don't hardcode this
        usersDb.addOrEditUserInClassroom(classroom.org_id, classroom.number, user.uid, "student")

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(null)
    }
}