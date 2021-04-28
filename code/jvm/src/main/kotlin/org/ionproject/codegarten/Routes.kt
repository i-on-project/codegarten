package org.ionproject.codegarten

import org.ionproject.codegarten.responses.siren.SirenLink
import org.springframework.http.MediaType
import org.springframework.web.util.UriTemplate
import java.net.URI

object Routes {
    val INPUT_CONTENT_TYPE = MediaType.APPLICATION_FORM_URLENCODED

    const val SCHEME = "http"
    const val PORT = "8080"
    const val HOST = "localhost:$PORT"
    const val API_BASE_URI = "/api"
    const val IM_BASE_URI = "/im"

    const val PAGE_TEMPLATE_QUERY = "{?page,limit}"
    const val PAGE_QUERY = "?page={page}&limit={limit}"

    const val ORG_PARAM = "orgId"
    const val CLASSROOM_PARAM = "classroomNumber"
    const val ASSIGNMENT_PARAM = "assignmentNumber"
    const val DELIVERY_PARAM = "deliveryNumber"
    const val USER_PARAM = "userId"
    const val CLIENT_ID_PARAM = "client_id"
    const val INSTALLATION_ID_PARAM = "installation_id"
    const val SETUP_ACTION_PARAM = "setup_action"
    const val STATE_PARAM = "state"
    const val CODE_PARAM = "code"
    const val ERR_PARAM = "error"


    // Error
    const val ERROR_HREF = "/error"


    // Interaction Manager routes
    const val AUTH_HREF = "$IM_BASE_URI/oauth"
    const val AUTH_CODE_HREF = "$AUTH_HREF/authorize"
    const val AUTH_CODE_CB_HREF = "$AUTH_CODE_HREF/cb"
    const val AUTH_TOKEN_HREF = "$API_BASE_URI/oauth/access_token"
    const val GH_INSTALLATIONS_HREF = "$IM_BASE_URI/github/install"
    const val GH_INSTALLATIONS_CB_HREF = "$GH_INSTALLATIONS_HREF/cb"


    // Users
    const val USER_HREF = "$API_BASE_URI/user"
    const val USER_BY_ID_HREF = "$API_BASE_URI/users/{$USER_PARAM}"

    val USER_BY_ID_HREF_TEMPLATE = UriTemplate(USER_BY_ID_HREF)

    fun getUserByIdUri(userId: Int) = USER_BY_ID_HREF_TEMPLATE.expand(userId)


    // Organizations
    const val ORGS_HREF = "$API_BASE_URI/orgs"
    const val ORG_BY_ID_HREF = "$ORGS_HREF/{$ORG_PARAM}"

    val ORG_BY_ID_HREF_TEMPLATE = UriTemplate(ORG_BY_ID_HREF)

    fun getOrgByIdUri(orgId: Int) = ORG_BY_ID_HREF_TEMPLATE.expand(orgId)


    // Classrooms
    const val CLASSROOMS_HREF = "$ORG_BY_ID_HREF/classrooms"
    const val CLASSROOM_BY_NUMBER_HREF = "$CLASSROOMS_HREF/{$CLASSROOM_PARAM}"

    val CLASSROOM_HREF_TEMPLATE = UriTemplate(CLASSROOMS_HREF)
    val CLASSROOM_BY_NUMBER_HREF_TEMPLATE = UriTemplate(CLASSROOM_BY_NUMBER_HREF)

    fun getClassroomsUri(orgId: Int) = CLASSROOM_HREF_TEMPLATE.expand(orgId)
    fun getClassroomByNumberUri(orgId: Int, classroomNumber: Int) =
        CLASSROOM_BY_NUMBER_HREF_TEMPLATE.expand(orgId, classroomNumber)


    // Assignments
    const val ASSIGNMENTS_HREF = "$CLASSROOM_BY_NUMBER_HREF/assignments"
    const val ASSIGNMENT_BY_NUMBER_HREF = "$ASSIGNMENTS_HREF/{$ASSIGNMENT_PARAM}"
    const val ASSIGNMENTS_OF_USER_HREF = "$USER_HREF/assignments"

    val ASSIGNMENTS_HREF_TEMPLATE = UriTemplate(ASSIGNMENTS_HREF)
    val ASSIGNMENT_BY_NUMBER_HREF_TEMPLATE = UriTemplate(ASSIGNMENT_BY_NUMBER_HREF)

    fun getAssignmentsUri(orgId: Int, classroomNumber: Int) = ASSIGNMENTS_HREF_TEMPLATE.expand(orgId, classroomNumber)
    fun getAssignmentByNumberUri(orgId: Int, classroomNumber: Int, assignmentNumber: Int) =
        ASSIGNMENT_BY_NUMBER_HREF_TEMPLATE.expand(orgId, classroomNumber, assignmentNumber)


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

    val USER_OF_CLASSROOM_HREF_TEMPLATE = UriTemplate(USER_OF_CLASSROOM_HREF)

    fun getUserOfClassroomUri(orgId: Int, classroomNumber: Int, userId: Int) =
        USER_OF_CLASSROOM_HREF_TEMPLATE.expand(orgId, classroomNumber, userId)


    // User Assignment
    const val USERS_OF_ASSIGNMENT_HREF = "$ASSIGNMENT_BY_NUMBER_HREF/users"
    const val USER_OF_ASSIGNMENT_HREF = "$USERS_OF_ASSIGNMENT_HREF/{$USER_PARAM}"

    val USERS_OF_ASSIGNMENT_HREF_TEMPLATE = UriTemplate(USERS_OF_ASSIGNMENT_HREF)
    val USER_OF_ASSIGNMENT_HREF_TEMPLATE = UriTemplate(USER_OF_ASSIGNMENT_HREF)

    fun getUsersOfAssignmentUri(orgId: Int, classroomNumber: Int, assignmentNumber: Int) =
        USERS_OF_ASSIGNMENT_HREF_TEMPLATE.expand(orgId, classroomNumber, assignmentNumber)
    fun getUserOfAssignmentUri(orgId: Int, classroomNumber: Int, assignmentNumber: Int, userId: Int) =
        USER_OF_ASSIGNMENT_HREF_TEMPLATE.expand(orgId, classroomNumber, assignmentNumber, userId)


    // User Deliveries
    const val DELIVERIES_OF_USER_HREF = "$USER_OF_ASSIGNMENT_HREF/deliveries"

    val DELIVERIES_OF_USER_HREF_TEMPLATE = UriTemplate(DELIVERIES_OF_USER_HREF)

    fun getDeliveriesOfUserUri(orgId: Int, classroomNumber: Int, assignmentNumber: Int, userId: Int) =
        DELIVERIES_OF_USER_HREF_TEMPLATE.expand(orgId, classroomNumber, assignmentNumber, userId)


    // Helpers
    fun createSirenLinkListForPagination(uri: URI, page: Int, limit: Int, collectionSize: Int): List<SirenLink> {
        val toReturn = mutableListOf(
            SirenLink(listOf("self"), UriTemplate("${uri}$PAGE_QUERY").expand(page, limit)),
            SirenLink(listOf("page"), hrefTemplate = "${uri}$PAGE_TEMPLATE_QUERY")
        )

        if (page > 0 && collectionSize > 0)
            toReturn.add(
                SirenLink(
                    listOf("previous"),
                    UriTemplate("${uri}$PAGE_QUERY")
                        .expand(page - 1, limit)
                )
            )

        if (collectionSize > ((page + 1) * limit))
            toReturn.add(
                SirenLink(
                    listOf("next"),
                    UriTemplate("${uri}$PAGE_QUERY")
                        .expand(page + 1, limit)
                )
            )

        return toReturn
    }

    fun URI.includeHost() = URI(SCHEME.toLowerCase(), HOST, this.path, this.query, this.fragment)
}