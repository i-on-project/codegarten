package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes.ASSIGNMENT_ID_PARAM
import org.ionproject.codegarten.Routes.ASSIGNMENT_PARAM
import org.ionproject.codegarten.Routes.CLASSROOM_ID_PARAM
import org.ionproject.codegarten.Routes.CLASSROOM_PARAM
import org.ionproject.codegarten.Routes.INVITE_CODE_PARAM
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.Routes.PARTICIPANTS_OF_ASSIGNMENT_HREF
import org.ionproject.codegarten.Routes.PARTICIPANT_OF_ASSIGNMENT_HREF
import org.ionproject.codegarten.Routes.PARTICIPANT_PARAM
import org.ionproject.codegarten.Routes.PARTICIPATION_IN_ASSIGNMENT_OF_USER_HREF
import org.ionproject.codegarten.Routes.PARTICIPATION_IN_CLASSROOM_OF_USER_HREF
import org.ionproject.codegarten.Routes.SELF_PARAM
import org.ionproject.codegarten.Routes.USER_INVITE_HREF
import org.ionproject.codegarten.Routes.createSirenLinkListForPagination
import org.ionproject.codegarten.Routes.getAssignmentByNumberUri
import org.ionproject.codegarten.Routes.getClassroomByNumberUri
import org.ionproject.codegarten.Routes.getDeliveriesOfParticipantUri
import org.ionproject.codegarten.Routes.getOrgByIdUri
import org.ionproject.codegarten.Routes.getParticipantsOfAssignmentUri
import org.ionproject.codegarten.Routes.getParticipationInAssignmentOfUserUri
import org.ionproject.codegarten.Routes.getParticipationInClassroomOfUserUri
import org.ionproject.codegarten.Routes.getTeamByNumberUri
import org.ionproject.codegarten.Routes.getUserByIdUri
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.controllers.api.actions.ParticipantActions
import org.ionproject.codegarten.controllers.models.OutputModel
import org.ionproject.codegarten.controllers.models.ParticipantItemOutputModel
import org.ionproject.codegarten.controllers.models.ParticipantOutputModel
import org.ionproject.codegarten.controllers.models.ParticipantTypes
import org.ionproject.codegarten.controllers.models.ParticipantsOutputModel
import org.ionproject.codegarten.controllers.models.UserInvitationInputModel
import org.ionproject.codegarten.database.PsqlErrorCode
import org.ionproject.codegarten.database.dto.Assignment
import org.ionproject.codegarten.database.dto.Installation
import org.ionproject.codegarten.database.dto.InviteCode
import org.ionproject.codegarten.database.dto.Team
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserClassroom
import org.ionproject.codegarten.database.dto.UserClassroomMembership
import org.ionproject.codegarten.database.dto.UserClassroomMembership.NOT_A_MEMBER
import org.ionproject.codegarten.database.dto.UserClassroomMembership.STUDENT
import org.ionproject.codegarten.database.dto.UserClassroomMembership.TEACHER
import org.ionproject.codegarten.database.dto.isFromAssignment
import org.ionproject.codegarten.database.dto.isGroupAssignment
import org.ionproject.codegarten.database.dto.isIndividualAssignment
import org.ionproject.codegarten.database.getPsqlErrorCode
import org.ionproject.codegarten.database.helpers.AssignmentsDb
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
import org.ionproject.codegarten.remote.github.GitHubInterface
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
import org.jdbi.v3.core.JdbiException
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
class ParticipantsController(
    val usersDb: UsersDb,
    val teamsDb: TeamsDb,
    val assignmentsDb: AssignmentsDb,
    val gitHub: GitHubInterface,
    val cryptoUtils: CryptoUtils,
) {

    @RequiresUserAuth
    @GetMapping(PARTICIPATION_IN_ASSIGNMENT_OF_USER_HREF)
    fun getUserParticipationInAssignment(
        @PathVariable(name = ASSIGNMENT_ID_PARAM) assignmentId: Int,
        user: User,
    ): ResponseEntity<Response> {
        val assignment = assignmentsDb.getAssignmentById(assignmentId)

        val participation =
            if (assignment.isIndividualAssignment()) {
                val userAssignment = usersDb.getUserAssignment(assignment.aid, user.uid)
                val repo = gitHub.getRepoById(userAssignment.repo_id, user.gh_token)

                ParticipantOutputModel(
                    type = ParticipantTypes.USER.type,
                    id = user.uid,
                    name = user.name,
                ).toSirenObject(
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM), getParticipationInAssignmentOfUserUri(assignment.aid).includeHost()),
                        SirenLink(listOf("repo"), URI(repo.html_url)),
                        SirenLink(listOf("deliveries"),
                            getDeliveriesOfParticipantUri(assignment.org_id, assignment.classroom_number, assignment.number, user.uid).includeHost()
                        ),
                        SirenLink(listOf("user"), getUserByIdUri(user.uid).includeHost()),
                        SirenLink(listOf("assignment"),
                            getAssignmentByNumberUri(assignment.org_id, assignment.classroom_number, assignment.number).includeHost()
                        ),
                        SirenLink(listOf("classroom"),
                            getClassroomByNumberUri(assignment.org_id, assignment.classroom_number).includeHost()
                        ),
                        SirenLink(listOf("organization"), getOrgByIdUri(assignment.org_id).includeHost()),
                    )
                )
            } else {
                // TODO: User may be registered multiple times in an assignment using different teams
                // Group assignment
                val team = teamsDb.getUserTeamInAssignment(assignment.aid, user.uid)
                val teamAssignment = teamsDb.getTeamAssignment(assignment.aid, team.tid)
                val repo = gitHub.getRepoById(teamAssignment.repo_id, user.gh_token)
                ParticipantOutputModel(
                    type = ParticipantTypes.TEAM.type,
                    id = team.tid,
                    name = team.name,
                ).toSirenObject(
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM), getParticipationInAssignmentOfUserUri(assignment.aid).includeHost()),
                        SirenLink(listOf("repo"), URI(repo.html_url)),
                        SirenLink(listOf("deliveries"),
                            getDeliveriesOfParticipantUri(assignment.org_id, assignment.classroom_number, assignment.number, team.number).includeHost()
                        ),
                        SirenLink(listOf("team"), getTeamByNumberUri(team.org_id, team.classroom_number, team.number).includeHost()),
                        SirenLink(listOf("assignment"),
                            getAssignmentByNumberUri(assignment.org_id, assignment.classroom_number, assignment.number).includeHost()
                        ),
                        SirenLink(listOf("classroom"),
                            getClassroomByNumberUri(assignment.org_id, assignment.classroom_number).includeHost()
                        ),
                        SirenLink(listOf("organization"), getOrgByIdUri(assignment.org_id).includeHost()),
                    )
                )
            }

        return participation.toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserAuth
    @GetMapping(PARTICIPATION_IN_CLASSROOM_OF_USER_HREF)
    fun getUserParticipationInClassroom(
        @PathVariable(name = CLASSROOM_ID_PARAM) classroomId: Int,
        user: User,
    ): ResponseEntity<Response> {
        val userClassroom = usersDb.getUserMembershipInClassroom(classroomId, user.uid)
        val classroom = userClassroom.classroom

        if (userClassroom.role == NOT_A_MEMBER) throw NotFoundException("User has no participation in classroom")

        return ParticipantOutputModel(
            type = userClassroom.role.name.toLowerCase(),
            id = user.uid,
            name = user.name,
        ).toSirenObject(
            links = listOf(
                SirenLink(listOf(SELF_PARAM), getParticipationInClassroomOfUserUri(classroomId).includeHost()),
                SirenLink(listOf("user"), getUserByIdUri(user.uid).includeHost()),
                SirenLink(listOf("classroom"),
                    getClassroomByNumberUri(classroom.org_id, classroom.number).includeHost()
                ),
                SirenLink(listOf("organization"), getOrgByIdUri(classroom.org_id).includeHost()),
            )
        ).toResponseEntity(HttpStatus.OK)
    }

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
        // TODO: Add Repo Links to participants
        val isGroupAssignment = assignment.isGroupAssignment()
        val isTeacher = userClassroom.role == TEACHER

        val actions =
            if (isTeacher)
                listOf(
                    ParticipantActions.getAddParticipantToAssignment(orgId, classroomNumber, assignmentNumber, isGroupAssignment),
                    ParticipantActions.getRemoveParticipantFromAssignment(orgId, classroomNumber, assignmentNumber, isGroupAssignment)
                )
            else null

        val participantsSirenObject =
            if (isGroupAssignment)
                getTeamsInAssignment(
                    orgId, classroomNumber, assignmentNumber,
                    assignment.aid, isTeacher,
                    actions, pagination
                )
            else
                getUsersInAssignment(
                    orgId, classroomNumber, assignmentNumber,
                    isTeacher, actions, pagination
                )

        return participantsSirenObject.toResponseEntity(HttpStatus.OK)
    }

    private fun getUsersInAssignment(
        orgId: Int, classroomNumber: Int, assignmentNumber: Int,
        isTeacher: Boolean, actions: List<SirenAction>?,
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
                ParticipantItemOutputModel(
                    id = it.uid,
                    name = it.name
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOfNotNull(
                        SirenLink(listOf(SELF_PARAM), getUserByIdUri(it.uid).includeHost()),
                        SirenLink(listOf("avatar"), getGitHubUserAvatarUri(it.gh_id)),
                        if (isTeacher)
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
        assignmentId: Int, isTeacher: Boolean,
        actions: List<SirenAction>?, pagination: Pagination
    ): Siren<OutputModel> {
        val teams = teamsDb.getTeamsFromAssignment(assignmentId, pagination.page, pagination.limit)
        val teamsCount = teamsDb.getTeamsFromAssignmentCount(assignmentId)

        return ParticipantsOutputModel(
            participantsType = ParticipantTypes.TEAM.type,
            collectionSize = teamsCount,
            pageIndex = pagination.page,
            pageSize = teams.size
        ).toSirenObject(
            entities = teams.map {
                ParticipantItemOutputModel(
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
                val userMembershipInClassroom = usersDb.getUserMembershipInClassroom(orgId, assignment.classroom_number, participantId)

                if (userMembershipInClassroom.role == NOT_A_MEMBER) throw InvalidInputException("User is not in the classroom")
                if (userMembershipInClassroom.role == TEACHER) throw InvalidInputException("Cannot add a teacher to an assignment")
                val userToAdd = userMembershipInClassroom.user!!

                addUserToAssignment(orgId, assignment, userToAdd, installation.accessToken, user.gh_token)
            } else {
                val team = teamsDb.getTeam(assignment.classroom_id, participantId)
                addTeamToAssignment(orgId, assignment, team, installation.accessToken, user.gh_token)
            }

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location", repo.html_url)
            .body(null)
    }

    private fun addUserToAssignment(
        orgId: Int, assignment: Assignment, userToAdd: User,
        installationToken: String, userGhToken: String
    ): GitHubRepoResponse {
        if (usersDb.isUserInAssignment(assignment.aid, userToAdd.uid)) {
            throw InvalidInputException("User is already in assignment")
        }

        val org = gitHub.getOrgById(orgId, userGhToken)

        val repoName =
            generateCodeGartenRepoName(assignment.classroom_number, assignment.repo_prefix, userToAdd.name)

        try {
            val repo =
                if (assignment.repo_template == null) gitHub.createRepo(orgId, repoName, installationToken)
                else gitHub.createRepoFromTemplate(org.login, repoName, assignment.repo_template, installationToken)

            val ghUser = gitHub.getUser(userToAdd.gh_id, userGhToken)
            gitHub.addUserToRepo(repo.id, ghUser.login, installationToken)

            usersDb.addUserToAssignment(orgId, assignment.classroom_number, assignment.number, userToAdd.uid, repo.id)
            return repo
        } catch (ex: HttpRequestException) {
            if (ex.status == HttpStatus.UNPROCESSABLE_ENTITY.value())
                throw InvalidInputException("User is already in assignment")
            else throw ex
        }
    }

    private fun addTeamToAssignment(
        orgId: Int, assignment: Assignment, team: Team,
        installationToken: String, userGhToken: String
    ): GitHubRepoResponse {

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
        if (assignment.isIndividualAssignment()) {
            if (userClassroom.role != TEACHER && user.uid != participantId) throw AuthorizationException("Not enough permissions to remove user")

            usersDb.deleteUserFromAssignment(orgId, classroomNumber, assignmentNumber, participantId)
        } else {
            // TODO: Should be tryGetUserTeamInAssignment to not respond with 404 early, since the user might not have permission
            val team = teamsDb.getUserTeamInAssignment(assignment.aid, user.uid)

            if (userClassroom.role != TEACHER && team.tid != participantId) throw AuthorizationException("Not enough permissions to remove team")
            teamsDb.deleteTeamFromAssignment(orgId, classroomNumber, assignmentNumber, participantId)
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }

    @RequiresGhAppInstallation
    @RequiresUserAuth
    @PutMapping(USER_INVITE_HREF)
    fun addAuthUserThroughInvite(
        @PathVariable(name = INVITE_CODE_PARAM) inviteCodePath: String,
        installation: Installation,
        inviteCode: InviteCode,
        user: User,
        @RequestBody input: UserInvitationInputModel?
    ): ResponseEntity<Any> {
        val isUserInClassroom = usersDb.getUserMembershipInClassroom(inviteCode.classroom_id, user.uid).role != UserClassroomMembership.NOT_A_MEMBER
        val ghUser = gitHub.getUser(user.gh_id, user.gh_token)

        val team =
            if (input?.teamId != null) {
                val maybeTeam = teamsDb.tryGetTeam(input.teamId)
                if (maybeTeam.isEmpty) throw InvalidInputException("Team does not exist")
                val team = maybeTeam.get()

                if (team.classroom_id != inviteCode.classroom_id)
                    throw InvalidInputException("Team does not exist in the classroom")

                team
            } else {
                null
            }

        // Invite user to organization if needed
        if (gitHub.getUserOrgMembership(inviteCode.org_id, user.gh_token).role == GitHubUserOrgRole.NOT_A_MEMBER) {
            gitHub.inviteUserToOrg(inviteCode.org_id, user.gh_id, team?.gh_id, installation.accessToken)
        }

        // Add user to classroom if needed
        if (!isUserInClassroom) {
            usersDb.addOrEditUserInClassroom(inviteCode.classroom_id, user.uid, STUDENT.name.toLowerCase())
        }

        // Add user/team to assignment
        var locationHeader: String? = null
        if (inviteCode.isFromAssignment()) {
            val assignment = assignmentsDb.getAssignmentById(inviteCode.assignment_id)
            if (assignment.isIndividualAssignment()) {
                locationHeader =
                    addUserToAssignment(inviteCode.org_id, assignment, user, installation.accessToken, user.gh_token).html_url
            } else if (assignment.isGroupAssignment()) {
                if (team == null) throw InvalidInputException("Missing teamId")

                if (teamsDb.isUserTeamInAssignment(assignment.aid, user.uid)) {
                    throw InvalidInputException("User is already in assignment")
                }

                // Check if team is already in assignment (repo already created)
                val maybeTeamAssignment = teamsDb.tryGetTeamAssignment(assignment.aid, team.tid)
                locationHeader =
                    if (maybeTeamAssignment.isPresent) {
                        val repoId = maybeTeamAssignment.get().repo_id
                        gitHub.getRepoById(repoId, installation.accessToken).html_url
                    } else {
                        addTeamToAssignment(inviteCode.org_id, assignment, team, installation.accessToken, user.gh_token).html_url
                    }
            }
        }

        // Add user to team
        if (team != null) {
            try {
                usersDb.addUserToTeam(team.tid, user.uid)
                gitHub.addUserToTeam(inviteCode.org_id, team.gh_id, ghUser.login, installation.accessToken)
            } catch (ex: JdbiException) {
                if (ex.getPsqlErrorCode() != PsqlErrorCode.UniqueViolation) throw ex
                // Ignore exception if user is already in team
            }
        }

        val toReturn = ResponseEntity
            .status(HttpStatus.CREATED)

        if (locationHeader != null)
            toReturn.header("Location", locationHeader)

        return toReturn.body(null)
    }
}