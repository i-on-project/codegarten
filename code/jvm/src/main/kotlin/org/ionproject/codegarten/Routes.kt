package org.ionproject.codegarten

import org.ionproject.codegarten.responses.siren.SirenLink
import org.springframework.http.MediaType
import org.springframework.web.util.UriTemplate
import java.net.URI

object Routes {
    val INPUT_CONTENT_TYPE = MediaType.APPLICATION_JSON

    const val SCHEME = "http"
    const val PORT = "8080"
    const val HOST = "localhost:$PORT"
    const val API_BASE_URI = "/api"
    const val IM_BASE_URI = "/im"

    const val DEFAULT_PAGE = 0
    const val DEFAULT_LIMIT = 10
    const val MAX_LIMIT = 100

    const val ORG_PARAM = "orgId"
    const val CLASSROOM_PARAM = "classroomNumber"
    const val CLASSROOM_ID_PARAM = "classroomId"
    const val TEAM_PARAM = "teamNumber"
    const val ASSIGNMENT_PARAM = "assignmentNumber"
    const val ASSIGNMENT_ID_PARAM = "assignmentId"
    const val DELIVERY_PARAM = "deliveryNumber"
    const val USER_PARAM = "userId"
    const val SEARCH_PARAM = "q"
    const val PARTICIPANT_PARAM = "participantId"
    const val INVITE_CODE_PARAM = "inviteCode"
    const val CLIENT_ID_PARAM = "client_id"
    const val INSTALLATION_ID_PARAM = "installation_id"
    const val STATE_PARAM = "state"
    const val CODE_PARAM = "code"
    const val ERR_PARAM = "error"

    const val PAGE_PARAM = "page"
    const val LIMIT_PARAM = "limit"
    const val NEXT_PAGE_PARAM = "next"
    const val PREVIOUS_PAGE_PARAM = "prev"
    const val SELF_PARAM = "self"

    // Pagination
    const val PAGE_TEMPLATE_QUERY = "{?$PAGE_PARAM,$LIMIT_PARAM}"
    const val PAGE_QUERY = "?page={$PAGE_PARAM}&limit={$LIMIT_PARAM}"

    // Error
    const val ERROR_HREF = "/error"


    // Interaction Manager routes
    const val IM_AUTH_HREF = "$IM_BASE_URI/oauth"
    const val AUTH_CODE_HREF = "$IM_AUTH_HREF/authorize"
    const val AUTH_CODE_CB_HREF = "$AUTH_CODE_HREF/cb"
    const val API_AUTH_HREF = "$API_BASE_URI/oauth"
    const val AUTH_TOKEN_HREF = "$API_AUTH_HREF/access_token"
    const val AUTH_REVOKE_HREF = "$API_AUTH_HREF/revoke"
    const val GH_INSTALLATIONS_HREF = "$IM_BASE_URI/github/install"
    const val GH_INSTALLATIONS_CB_HREF = "$GH_INSTALLATIONS_HREF/cb"


    // Users
    const val USER_HREF = "$API_BASE_URI/user"
    const val USER_INVITES_HREF = "$USER_HREF/invites"
    const val USER_INVITE_HREF = "$USER_INVITES_HREF/{$INVITE_CODE_PARAM}"
    const val USER_INVITE_CLASSROOM_HREF = "$USER_INVITE_HREF/classroom"
    const val USER_INVITE_CLASSROOM_TEAMS_HREF = "$USER_INVITE_CLASSROOM_HREF/teams"
    const val USER_BY_ID_HREF = "$API_BASE_URI/users/{$USER_PARAM}"

    val USER_BY_ID_HREF_TEMPLATE = UriTemplate(USER_BY_ID_HREF)
    val USER_INVITE_HREF_TEMPLATE = UriTemplate(USER_INVITE_HREF)
    val USER_INVITE_CLASSROOM_TEMPLATE = UriTemplate(USER_INVITE_CLASSROOM_HREF)
    val USER_INVITE_CLASSROOM_TEAMS_TEMPLATE = UriTemplate(USER_INVITE_CLASSROOM_TEAMS_HREF)

    fun getUserByIdUri(userId: Int) = USER_BY_ID_HREF_TEMPLATE.expand(userId)
    fun getUserInviteUri(invCode: String) = USER_INVITE_HREF_TEMPLATE.expand(invCode)
    fun getUserInviteClassroomUri(invCode: String) = USER_INVITE_CLASSROOM_TEMPLATE.expand(invCode)
    fun getUserInviteClassroomTeamsUri(invCode: String) = USER_INVITE_CLASSROOM_TEAMS_TEMPLATE.expand(invCode)


    // Organizations
    const val ORGS_HREF = "$API_BASE_URI/orgs"
    const val ORG_BY_ID_HREF = "$ORGS_HREF/{$ORG_PARAM}"

    val ORG_BY_ID_HREF_TEMPLATE = UriTemplate(ORG_BY_ID_HREF)

    fun getOrgByIdUri(orgId: Int) = ORG_BY_ID_HREF_TEMPLATE.expand(orgId)


    // Classrooms
    const val CLASSROOMS_HREF = "$ORG_BY_ID_HREF/classrooms"
    const val CLASSROOM_BY_NUMBER_HREF = "$CLASSROOMS_HREF/{$CLASSROOM_PARAM}"

    val CLASSROOMS_HREF_TEMPLATE = UriTemplate(CLASSROOMS_HREF)
    val CLASSROOM_BY_NUMBER_HREF_TEMPLATE = UriTemplate(CLASSROOM_BY_NUMBER_HREF)

    fun getClassroomsUri(orgId: Int) = CLASSROOMS_HREF_TEMPLATE.expand(orgId)
    fun getClassroomByNumberUri(orgId: Int, classroomNumber: Int) =
        CLASSROOM_BY_NUMBER_HREF_TEMPLATE.expand(orgId, classroomNumber)

    // Teams
    const val TEAMS_HREF = "$CLASSROOM_BY_NUMBER_HREF/teams"
    const val TEAM_BY_NUMBER_HREF = "$TEAMS_HREF/{$TEAM_PARAM}"

    val TEAMS_HREF_TEMPLATE = UriTemplate(TEAMS_HREF)
    val TEAM_BY_NUMBER_HREF_TEMPLATE = UriTemplate(TEAM_BY_NUMBER_HREF)

    fun getTeamsUri(orgId: Int, classroomNumber: Int) = TEAMS_HREF_TEMPLATE.expand(orgId, classroomNumber)
    fun getTeamByNumberUri(orgId: Int, classroomNumber: Int, teamNumber: Int) =
        TEAM_BY_NUMBER_HREF_TEMPLATE.expand(orgId, classroomNumber, teamNumber)


    // Assignments
    const val ASSIGNMENTS_HREF = "$CLASSROOM_BY_NUMBER_HREF/assignments"
    const val ASSIGNMENT_BY_NUMBER_HREF = "$ASSIGNMENTS_HREF/{$ASSIGNMENT_PARAM}"
    const val CLASSROOMS_OF_USER_HREF = "$USER_HREF/classrooms"
    const val CLASSROOM_OF_USER_HREF = "$CLASSROOMS_OF_USER_HREF/{$CLASSROOM_ID_PARAM}"
    const val PARTICIPATION_IN_CLASSROOM_OF_USER_HREF = "$CLASSROOM_OF_USER_HREF/participation"
    const val ASSIGNMENTS_OF_USER_HREF = "$USER_HREF/assignments"
    const val ASSIGNMENT_OF_USER_HREF = "$ASSIGNMENTS_OF_USER_HREF/{$ASSIGNMENT_ID_PARAM}"
    const val PARTICIPATION_IN_ASSIGNMENT_OF_USER_HREF = "$ASSIGNMENT_OF_USER_HREF/participation"

    val ASSIGNMENTS_HREF_TEMPLATE = UriTemplate(ASSIGNMENTS_HREF)
    val ASSIGNMENT_BY_NUMBER_HREF_TEMPLATE = UriTemplate(ASSIGNMENT_BY_NUMBER_HREF)
    val CLASSROOM_OF_USER_HREF_TEMPLATE = UriTemplate(CLASSROOM_OF_USER_HREF)
    val PARTICIPATION_IN_CLASSROOM_OF_USER_TEMPLATE = UriTemplate(PARTICIPATION_IN_CLASSROOM_OF_USER_HREF)
    val ASSIGNMENT_OF_USER_HREF_TEMPLATE = UriTemplate(ASSIGNMENT_OF_USER_HREF)
    val PARTICIPATION_IN_ASSIGNMENT_OF_USER_TEMPLATE = UriTemplate(PARTICIPATION_IN_ASSIGNMENT_OF_USER_HREF)

    fun getAssignmentsUri(orgId: Int, classroomNumber: Int) = ASSIGNMENTS_HREF_TEMPLATE.expand(orgId, classroomNumber)
    fun getAssignmentByNumberUri(orgId: Int, classroomNumber: Int, assignmentNumber: Int) =
        ASSIGNMENT_BY_NUMBER_HREF_TEMPLATE.expand(orgId, classroomNumber, assignmentNumber)
    fun getClassroomOfUserUri(classroomId: Int) =
        CLASSROOM_OF_USER_HREF_TEMPLATE.expand(classroomId)
    fun getParticipationInClassroomOfUserUri(classroomId: Int) =
        PARTICIPATION_IN_CLASSROOM_OF_USER_TEMPLATE.expand(classroomId)
    fun getAssignmentOfUserUri(assignmentId: Int) =
        ASSIGNMENT_OF_USER_HREF_TEMPLATE.expand(assignmentId)
    fun getParticipationInAssignmentOfUserUri(assignmentId: Int) =
        PARTICIPATION_IN_ASSIGNMENT_OF_USER_TEMPLATE.expand(assignmentId)


    // Deliveries
    const val DELIVERIES_HREF = "$ASSIGNMENT_BY_NUMBER_HREF/deliveries"
    const val DELIVERY_BY_NUMBER_HREF = "$DELIVERIES_HREF/{$DELIVERY_PARAM}"

    val DELIVERIES_HREF_TEMPLATE = UriTemplate(DELIVERIES_HREF)
    val DELIVERY_BY_NUMBER_HREF_TEMPLATE = UriTemplate(DELIVERY_BY_NUMBER_HREF)

    fun getDeliveriesUri(orgId: Int, classroomNumber: Int, assignmentNumber: Int) =
        DELIVERIES_HREF_TEMPLATE.expand(orgId, classroomNumber, assignmentNumber)
    fun getDeliveryByNumberUri(orgId: Int, classroomNumber: Int, assignmentNumber: Int, deliveryNumber: Int) =
        DELIVERY_BY_NUMBER_HREF_TEMPLATE.expand(orgId, classroomNumber, assignmentNumber, deliveryNumber)


    // User Classroom
    const val USERS_OF_CLASSROOM_HREF = "$CLASSROOM_BY_NUMBER_HREF/users"
    const val USER_OF_CLASSROOM_HREF = "$USERS_OF_CLASSROOM_HREF/{$USER_PARAM}"

    val USERS_OF_CLASSROOM_HREF_TEMPLATE = UriTemplate(USERS_OF_CLASSROOM_HREF)
    val USER_OF_CLASSROOM_HREF_TEMPLATE = UriTemplate(USER_OF_CLASSROOM_HREF)

    fun getUsersOfClassroomUri(orgId: Int, classroomNumber: Int) =
        USERS_OF_CLASSROOM_HREF_TEMPLATE.expand(orgId, classroomNumber)
    fun getUserOfClassroomUri(orgId: Int, classroomNumber: Int, userId: Int) =
        USER_OF_CLASSROOM_HREF_TEMPLATE.expand(orgId, classroomNumber, userId)


    // User Assignment
    const val PARTICIPANTS_OF_ASSIGNMENT_HREF = "$ASSIGNMENT_BY_NUMBER_HREF/participants"
    const val PARTICIPANT_OF_ASSIGNMENT_HREF = "$PARTICIPANTS_OF_ASSIGNMENT_HREF/{$PARTICIPANT_PARAM}"

    val PARTICIPANTS_OF_ASSIGNMENT_HREF_TEMPLATE = UriTemplate(PARTICIPANTS_OF_ASSIGNMENT_HREF)
    val PARTICIPANT_OF_ASSIGNMENT_HREF_TEMPLATE = UriTemplate(PARTICIPANT_OF_ASSIGNMENT_HREF)

    fun getParticipantsOfAssignmentUri(orgId: Int, classroomNumber: Int, assignmentNumber: Int) =
        PARTICIPANTS_OF_ASSIGNMENT_HREF_TEMPLATE.expand(orgId, classroomNumber, assignmentNumber)
    fun getParticipantOfAssignmentUri(orgId: Int, classroomNumber: Int, assignmentNumber: Int, participantId: Int) =
        PARTICIPANT_OF_ASSIGNMENT_HREF_TEMPLATE.expand(orgId, classroomNumber, assignmentNumber, participantId)


    // User Deliveries
    const val DELIVERIES_OF_PARTICIPANT_HREF = "$PARTICIPANT_OF_ASSIGNMENT_HREF/deliveries"
    const val DELIVERY_OF_PARTICIPANT_HREF = "$DELIVERIES_OF_PARTICIPANT_HREF/{$DELIVERY_PARAM}"

    val DELIVERIES_OF_PARTICIPANT_HREF_TEMPLATE = UriTemplate(DELIVERIES_OF_PARTICIPANT_HREF)
    val DELIVERY_OF_PARTICIPANT_HREF_TEMPLATE = UriTemplate(DELIVERY_OF_PARTICIPANT_HREF)

    fun getDeliveriesOfParticipantUri(orgId: Int, classroomNumber: Int, assignmentNumber: Int, participantId: Int) =
        DELIVERIES_OF_PARTICIPANT_HREF_TEMPLATE.expand(orgId, classroomNumber, assignmentNumber, participantId)
    fun getDeliveryOfParticipantUri(orgId: Int, classroomNumber: Int, assignmentNumber: Int, participantId: Int, deliveryNumber: Int) =
        DELIVERY_OF_PARTICIPANT_HREF_TEMPLATE.expand(orgId, classroomNumber, assignmentNumber, participantId, deliveryNumber)

    // User Team
    const val USERS_OF_TEAM_HREF = "$TEAM_BY_NUMBER_HREF/users"
    const val USER_OF_TEAM_HREF = "$USERS_OF_TEAM_HREF/{$USER_PARAM}"

    val USERS_OF_TEAM_HREF_TEMPLATE = UriTemplate(USERS_OF_TEAM_HREF)
    val USER_OF_TEAM_HREF_TEMPLATE = UriTemplate(USER_OF_TEAM_HREF)

    fun getUsersOfTeamUri(orgId: Int, classroomNumber: Int, teamNumber: Int) =
        USERS_OF_TEAM_HREF_TEMPLATE.expand(orgId, classroomNumber, teamNumber)
    fun getUserOfTeamUri(orgId: Int, classroomNumber: Int, teamNumber: Int, userId: Int) =
        USER_OF_TEAM_HREF_TEMPLATE.expand(orgId, classroomNumber, teamNumber, userId)

    // Helpers
    fun createSirenLinkListForPagination(uri: URI, page: Int, limit: Int, collectionSize: Int = Int.MAX_VALUE, pageSize: Int = Int.MAX_VALUE): List<SirenLink> {
        val toReturn = mutableListOf(
            SirenLink(listOf(SELF_PARAM), UriTemplate("${uri}$PAGE_QUERY").expand(page, limit)),
            SirenLink(listOf(PAGE_PARAM), hrefTemplate = UriTemplate("${uri}$PAGE_TEMPLATE_QUERY"))
        )

        if (page > 0 && collectionSize > 0)
            toReturn.add(
                SirenLink(
                    listOf(PREVIOUS_PAGE_PARAM),
                    UriTemplate("${uri}$PAGE_QUERY")
                        .expand(page - 1, limit)
                )
            )

        if (collectionSize > ((page + 1) * limit) && pageSize > 0)
            toReturn.add(
                SirenLink(
                    listOf(NEXT_PAGE_PARAM),
                    UriTemplate("${uri}$PAGE_QUERY")
                        .expand(page + 1, limit)
                )
            )

        return toReturn
    }

    fun URI.includeHost() = URI(SCHEME.toLowerCase(), HOST, this.path, this.query, this.fragment)
    fun UriTemplate.includeHost() = UriTemplate("${SCHEME.toLowerCase()}://${HOST}$this")
}