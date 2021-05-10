package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.Routes.INVITE_CODE_PARAM
import org.ionproject.codegarten.Routes.SELF_PARAM
import org.ionproject.codegarten.Routes.USER_INVITE_CLASSROOM_TEAMS_HREF
import org.ionproject.codegarten.Routes.USER_INVITE_HREF
import org.ionproject.codegarten.Routes.includeHost
import org.ionproject.codegarten.controllers.api.actions.InvitationActions
import org.ionproject.codegarten.controllers.models.AssignmentInvitationOutputModel
import org.ionproject.codegarten.controllers.models.ClassroomInvitationOutputModel
import org.ionproject.codegarten.controllers.models.TeamOutputModel
import org.ionproject.codegarten.controllers.models.TeamsOutputModel
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.isFromClassroom
import org.ionproject.codegarten.database.helpers.AssignmentsDb
import org.ionproject.codegarten.database.helpers.ClassroomsDb
import org.ionproject.codegarten.database.helpers.InviteCodesDb
import org.ionproject.codegarten.database.helpers.TeamsDb
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class InvitationsController(
    val teamsDb: TeamsDb,
    val classroomsDb: ClassroomsDb,
    val assignmentsDb: AssignmentsDb,
    val inviteCodesDb: InviteCodesDb,
    val gitHub: GitHubInterface,
    val cryptoUtils: CryptoUtils,
) {

    @RequiresUserAuth
    @GetMapping(USER_INVITE_HREF)
    fun getInviteCodeInfo(
        @PathVariable(name = INVITE_CODE_PARAM) inviteCodePath: String,
        user: User
    ): ResponseEntity<Response> {
        val inviteCode = inviteCodesDb.getInviteCode(inviteCodePath)

        val classroom = classroomsDb.getClassroomById(inviteCode.classroom_id)
        val org = gitHub.getOrgById(inviteCode.org_id, user.gh_token)

        val toReturn =
            if (inviteCode.isFromClassroom()) {
                ClassroomInvitationOutputModel(
                    id = classroom.cid,
                    name = classroom.name,
                    description = classroom.description,
                    organization = org.login,
                ).toSirenObject(
                    actions = listOf(InvitationActions.getJoinClassroomInviteAction(inviteCodePath)),
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM), Routes.getUserInviteUri(inviteCodePath).includeHost()),
                        SirenLink(listOf("teams"), Routes.getUserInviteClassroomTeamsUri(inviteCodePath).includeHost()),
                        SirenLink(listOf("classroom"), Routes.getClassroomByNumberUri(classroom.org_id, classroom.number))
                    )
                )
            } else {
                // Invite is from assignment
                val assignment = assignmentsDb.getAssignmentById(inviteCode.assignment_id)
                AssignmentInvitationOutputModel(
                    id = assignment.aid,
                    name = assignment.name,
                    description = assignment.description,
                    type = assignment.type,
                    classroom = assignment.classroom_name,
                    organization = org.login
                ).toSirenObject(
                    actions = listOf(InvitationActions.getJoinAssignmentInviteAction(inviteCodePath)),
                    links = listOf(
                        SirenLink(listOf(SELF_PARAM), Routes.getUserInviteUri(inviteCodePath).includeHost()),
                        SirenLink(listOf("teams"), Routes.getUserInviteClassroomTeamsUri(inviteCodePath).includeHost()),
                        SirenLink(listOf("assignment"),
                            Routes.getAssignmentByNumberUri(assignment.org_id, assignment.classroom_number, assignment.number)
                        )
                    )
                )
            }

        return toReturn.toResponseEntity(HttpStatus.OK)
    }

    @RequiresUserAuth
    @GetMapping(USER_INVITE_CLASSROOM_TEAMS_HREF)
    fun getTeamsOfClassroomInvite(
        @PathVariable(name = INVITE_CODE_PARAM) inviteCodePath: String,
        pagination: Pagination,
        user: User
    ): ResponseEntity<Response> {
        val inviteCode = inviteCodesDb.getInviteCode(inviteCodePath)

        val teamsCount = teamsDb.getTeamsOfClassroomCount(inviteCode.classroom_id)
        val teams = teamsDb.getTeamsOfClassroom(inviteCode.classroom_id, pagination.page, pagination.limit)
        val org = gitHub.getOrgById(inviteCode.org_id, user.gh_token)

        return TeamsOutputModel(
            collectionSize = teamsCount,
            pageIndex = pagination.page,
            pageSize = teams.size,
        ).toSirenObject(
            entities = teams.map {
                TeamOutputModel(
                    id = it.tid,
                    number = it.number,
                    name = it.name,
                    classroom = it.classroom_name,
                    organization = org.login
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOf(
                        SirenLink(listOf("avatar"), GitHubRoutes.getGitHubTeamAvatarUri(it.gh_id)),
                    )
                )
            },
            links = Routes.createSirenLinkListForPagination(
                Routes.getUserInviteClassroomTeamsUri(inviteCodePath).includeHost(),
                pagination.page,
                pagination.limit,
                teamsCount
            ) + listOf(
                SirenLink(listOf("invite"), Routes.getUserInviteUri(inviteCodePath).includeHost()),
            )
        ).toResponseEntity(HttpStatus.OK)
    }
}