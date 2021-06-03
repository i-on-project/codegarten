package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.Routes.CLASSROOM_PARAM
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.Routes.SELF_PARAM
import org.ionproject.codegarten.Routes.TEAMS_HREF
import org.ionproject.codegarten.Routes.TEAM_BY_NUMBER_HREF
import org.ionproject.codegarten.Routes.TEAM_PARAM
import org.ionproject.codegarten.Routes.getClassroomByNumberUri
import org.ionproject.codegarten.Routes.getOrgByIdUri
import org.ionproject.codegarten.Routes.getTeamByNumberUri
import org.ionproject.codegarten.Routes.getTeamsUri
import org.ionproject.codegarten.Routes.getUsersOfTeamUri
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.controllers.api.actions.TeamActions.getCreateTeamAction
import org.ionproject.codegarten.controllers.api.actions.TeamActions.getDeleteTeamAction
import org.ionproject.codegarten.controllers.api.actions.TeamActions.getEditTeamAction
import org.ionproject.codegarten.controllers.models.TeamCreateInputModel
import org.ionproject.codegarten.controllers.models.TeamEditInputModel
import org.ionproject.codegarten.controllers.models.TeamItemOutputModel
import org.ionproject.codegarten.controllers.models.TeamOutputModel
import org.ionproject.codegarten.controllers.models.TeamsOutputModel
import org.ionproject.codegarten.database.dto.Installation
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserClassroom
import org.ionproject.codegarten.database.dto.UserClassroomMembership.TEACHER
import org.ionproject.codegarten.database.helpers.TeamsDb
import org.ionproject.codegarten.exceptions.ForbiddenException
import org.ionproject.codegarten.exceptions.HttpRequestException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.pipeline.argumentresolvers.Pagination
import org.ionproject.codegarten.pipeline.interceptors.RequiresGhAppInstallation
import org.ionproject.codegarten.pipeline.interceptors.RequiresUserInClassroom
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.GitHubRoutes.generateCodeGartenTeamName
import org.ionproject.codegarten.remote.github.GitHubRoutes.getGitHubTeamAvatarUri
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
import java.net.URI

@RestController
class TeamsController(
    val teamsDb: TeamsDb,
    val gitHub: GitHubInterface
) {

    @RequiresUserInClassroom
    @GetMapping(TEAMS_HREF)
    fun getClassroomTeams(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        pagination: Pagination,
        user: User,
        userClassroom: UserClassroom
    ): ResponseEntity<Response> {
        val teamsCount: Int
        val teams =
            if (userClassroom.role == TEACHER) {
                teamsCount = teamsDb.getTeamsOfClassroomCount(orgId, classroomNumber)
                teamsDb.getTeamsOfClassroom(orgId, classroomNumber, pagination.page, pagination.limit)
            } else {
                teamsCount = teamsDb.getTeamsOfClassroomOfUserCount(orgId, classroomNumber, user.uid)
                teamsDb.getTeamsOfClassroomOfUser(orgId, classroomNumber, user.uid, pagination.page, pagination.limit)
            }

        val org = gitHub.getOrgById(orgId, user.gh_token)

        val actions =
            if (userClassroom.role == TEACHER)
                listOf(getCreateTeamAction(orgId, classroomNumber))
            else
                null

        return TeamsOutputModel(
            classroom = userClassroom.classroom.name,
            organization = org.login,
            collectionSize = teamsCount,
            pageIndex = pagination.page,
            pageSize = teams.size,
        ).toSirenObject(
            entities = teams.map {
                TeamItemOutputModel(
                    id = it.tid,
                    number = it.number,
                    name = it.name,
                    classroom = it.classroom_name,
                    organization = org.login
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM), getTeamByNumberUri(orgId, classroomNumber, it.number).includeHost()),
                        SirenLink(listOf("avatar"), getGitHubTeamAvatarUri(it.gh_id)),
                        SirenLink(listOf("users"), getUsersOfTeamUri(orgId, classroomNumber, it.number).includeHost()),
                        SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                        SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()),
                        SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
                    )
                )
            },
            actions = actions,
            links = Routes.createSirenLinkListForPagination(
                Routes.getClassroomsUri(orgId).includeHost(),
                pagination.page,
                pagination.limit,
                teamsCount
            ) + listOf(
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()),
                SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
            )
        ).toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserInClassroom
    @GetMapping(TEAM_BY_NUMBER_HREF)
    fun getClassroomTeam(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = TEAM_PARAM) teamNumber: Int,
        user: User,
        userClassroom: UserClassroom
    ): ResponseEntity<Response> {
        val team = teamsDb.getTeam(orgId, classroomNumber, teamNumber)
        val isMember = teamsDb.isUserInTeam(team.tid, user.uid)
        if (userClassroom.role != TEACHER && !isMember)
            throw ForbiddenException("User is not in team")

        val org = gitHub.getOrgById(orgId, user.gh_token)
        val ghTeam = gitHub.getTeam(orgId, team.gh_id, user.gh_token)

        val actions =
            if (userClassroom.role == TEACHER)
                listOf(
                    getEditTeamAction(orgId, classroomNumber, teamNumber),
                    getDeleteTeamAction(orgId, classroomNumber, teamNumber)
                )
            else
                null

        return TeamOutputModel(
            id = team.tid,
            number = team.number,
            name = team.name,
            gitHubName = ghTeam.name,
            isMember = isMember,
            classroom = team.classroom_name,
            organization = ghTeam.organization.login
        ).toSirenObject(
            actions = actions,
            links = listOf(
                SirenLink(listOf(SELF_PARAM), getTeamByNumberUri(orgId, classroomNumber, team.number).includeHost()),
                SirenLink(listOf("github"), URI(ghTeam.html_url)),
                SirenLink(listOf("avatar"), getGitHubTeamAvatarUri(team.gh_id)),
                SirenLink(listOf("users"), getUsersOfTeamUri(orgId, classroomNumber, team.number).includeHost()),
                SirenLink(listOf("teams"), getTeamsUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()),
                SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
            )
        ).toResponseEntity(HttpStatus.OK)

    }

    @RequiresGhAppInstallation
    @RequiresUserInClassroom
    @PostMapping(TEAMS_HREF)
    fun createTeam(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        user: User,
        userClassroom: UserClassroom,
        installation: Installation,
        @RequestBody input: TeamCreateInputModel?
    ): ResponseEntity<Response> {
        if (userClassroom.role != TEACHER) throw ForbiddenException("User is not a teacher")

        if (input == null) throw InvalidInputException("Missing body")
        if (input.name == null) throw InvalidInputException("Missing name")

        val org = gitHub.getOrgById(orgId, user.gh_token)
        val gitHubName = generateCodeGartenTeamName(classroomNumber, input.name)
        val ghTeam = try {
            gitHub.createTeam(orgId, gitHubName, installation.accessToken)
        } catch(e: HttpRequestException) {
            if (e.status == HttpStatus.UNPROCESSABLE_ENTITY.value())
                throw InvalidInputException("Team name must be unique in the classroom")
            else throw e
        }
        val createdTeam = teamsDb.createTeam(userClassroom.classroom.cid, input.name, ghTeam.id)

        return TeamOutputModel(
            id = createdTeam.tid,
            number = createdTeam.number,
            name = createdTeam.name,
            gitHubName = ghTeam.name,
            isMember = false,
            classroom = userClassroom.classroom.name,
            organization = ghTeam.organization.login
        ).toSirenObject(
            actions = listOf(
                getEditTeamAction(orgId, classroomNumber, createdTeam.number),
                getDeleteTeamAction(orgId, classroomNumber, createdTeam.number)
            ),
            links = listOf(
                SirenLink(listOf(SELF_PARAM), getTeamByNumberUri(orgId, classroomNumber, createdTeam.number).includeHost()),
                SirenLink(listOf("github"), URI(ghTeam.html_url)),
                SirenLink(listOf("avatar"), getGitHubTeamAvatarUri(createdTeam.gh_id)),
                SirenLink(listOf("users"), getUsersOfTeamUri(orgId, classroomNumber, createdTeam.number).includeHost()),
                SirenLink(listOf("teams"), getTeamsUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("classroom"), getClassroomByNumberUri(orgId, classroomNumber).includeHost()),
                SirenLink(listOf("organization"), getOrgByIdUri(orgId).includeHost()),
                SirenLink(listOf("organizationGitHub"), getGithubLoginUri(org.login))
            )
        ).toResponseEntity(HttpStatus.CREATED, mapOf(
            "Location" to listOf(getTeamByNumberUri(orgId, classroomNumber, createdTeam.number).includeHost().toString())
        ))
    }

    @RequiresUserInClassroom
    @PutMapping(TEAM_BY_NUMBER_HREF)
    fun editTeam(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = TEAM_PARAM) teamNumber: Int,
        user: User,
        userClassroom: UserClassroom,
        @RequestBody input: TeamEditInputModel?
    ): ResponseEntity<Response> {
        if (userClassroom.role != TEACHER) throw ForbiddenException("User is not a teacher")

        if (input == null) throw InvalidInputException("Missing body")
        if (input.name == null) throw InvalidInputException("Missing name")

        teamsDb.editTeam(userClassroom.classroom.cid, teamNumber, input.name)
        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Location", getTeamByNumberUri(orgId, classroomNumber, teamNumber).includeHost().toString())
            .body(null)
    }

    @RequiresGhAppInstallation
    @RequiresUserInClassroom
    @DeleteMapping(TEAM_BY_NUMBER_HREF)
    fun deleteTeam(
        @PathVariable(name = ORG_PARAM) orgId: Int,
        @PathVariable(name = CLASSROOM_PARAM) classroomNumber: Int,
        @PathVariable(name = TEAM_PARAM) teamNumber: Int,
        installation: Installation,
        user: User,
        userClassroom: UserClassroom
    ): ResponseEntity<Response> {
        if (userClassroom.role != TEACHER) throw ForbiddenException("User is not a teacher")

        val team = teamsDb.getTeam(orgId, classroomNumber, teamNumber)

        gitHub.deleteTeam(orgId, team.gh_id, installation.accessToken)
        teamsDb.deleteTeam(team.tid)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(null)
    }
}