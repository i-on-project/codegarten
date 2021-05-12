package org.ionproject.codegarten.pipeline.interceptors

import org.ionproject.codegarten.Routes.ASSIGNMENT_PARAM
import org.ionproject.codegarten.Routes.CLASSROOM_PARAM
import org.ionproject.codegarten.Routes.ORG_PARAM
import org.ionproject.codegarten.auth.AuthHeaderValidator
import org.ionproject.codegarten.auth.AuthHeaderValidator.AUTH_HEADER
import org.ionproject.codegarten.database.dto.AccessToken
import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.database.dto.UserClassroomMembership
import org.ionproject.codegarten.database.dto.isGroupAssignment
import org.ionproject.codegarten.database.dto.isIndividualAssignment
import org.ionproject.codegarten.database.helpers.AccessTokensDb
import org.ionproject.codegarten.database.helpers.AssignmentsDb
import org.ionproject.codegarten.database.helpers.TeamsDb
import org.ionproject.codegarten.database.helpers.UsersDb
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.ionproject.codegarten.exceptions.InvalidInputException
import org.ionproject.codegarten.exceptions.NotFoundException
import org.ionproject.codegarten.remote.github.GitHubInterface
import org.ionproject.codegarten.remote.github.responses.GitHubUserOrgRole
import org.ionproject.codegarten.utils.CryptoUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.HandlerMapping
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Target(AnnotationTarget.FUNCTION)
annotation class RequiresUserAuth

@Target(AnnotationTarget.FUNCTION)
annotation class RequiresUserInOrg

@Target(AnnotationTarget.FUNCTION)
annotation class RequiresUserInClassroom

@Target(AnnotationTarget.FUNCTION)
annotation class RequiresUserInAssignment

const val USER_ATTRIBUTE = "user-attribute"
const val ORG_MEMBERSHIP_ATTRIBUTE = "org-membership-attribute"
const val CLASSROOM_MEMBERSHIP_ATTRIBUTE = "classroom-membership-attribute"
const val ASSIGNMENT_ATTRIBUTE = "assignment-attribute"

@Component
class AuthorizationInterceptor(
    val tokensDb: AccessTokensDb,
    val assignmentsDb: AssignmentsDb,
    val teamsDb: TeamsDb,
    val usersDb: UsersDb,
    val gitHubInterface: GitHubInterface,
    val cryptoUtils: CryptoUtils
) : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(AuthorizationInterceptor::class.java)

    fun getAndVerifyUser(authorizationHeader: String?): User {
        val encryptedUser = AuthHeaderValidator.validate(authorizationHeader) { token ->
            val accessToken: AccessToken
            try {
                accessToken = tokensDb.getAccessToken(cryptoUtils.hash(token))
            } catch(e: NotFoundException) {
                throw AuthorizationException("Bad credentials")
            }

            if (accessToken.expiration_date.isBefore(OffsetDateTime.now())) {
                throw AuthorizationException("Access token has expired at ${accessToken.expiration_date}")
            }

            usersDb.getUserById(accessToken.user_id)
        }

        return User(
            encryptedUser.uid,
            encryptedUser.name,
            encryptedUser.gh_id,
            cryptoUtils.decrypt(encryptedUser.gh_token)
        )
    }

    fun getAndVerifyOrgMembership(orgId: Int, user: User): GitHubUserOrgRole {
        val membership = gitHubInterface.getUserOrgMembership(
            orgId,
            user.gh_token
        )

        if (membership.role == GitHubUserOrgRole.NOT_A_MEMBER) {
            throw AuthorizationException("User is not in the organization")
        }

        return membership.role
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val routeHandler = handler as? HandlerMethod ?: return true

        val requiresUserInAssignment = routeHandler.hasMethodAnnotation(RequiresUserInAssignment::class.java)
        val requiresUserInClassroom = routeHandler.hasMethodAnnotation(RequiresUserInClassroom::class.java) || requiresUserInAssignment
        val requiresUserInOrg = routeHandler.hasMethodAnnotation(RequiresUserInOrg::class.java) || requiresUserInClassroom
        val requiresUserAuth = routeHandler.hasMethodAnnotation(RequiresUserAuth::class.java) || requiresUserInOrg

        if (requiresUserAuth) {
            logger.info("PreHandle with handler ${handler.javaClass.name} requires authentication")
            val user = getAndVerifyUser(request.getHeader(AUTH_HEADER)?.trim())

            request.setAttribute(USER_ATTRIBUTE, user)
            logger.info("User with name '${user.name}' has a valid access_token")

            if (requiresUserInOrg) {
                // Get the path variables
                val pathVars = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE) as Map<String, String>

                val orgId = pathVars[ORG_PARAM]?.toIntOrNull() ?: throw InvalidInputException("Invalid organization id")
                val orgMembership = getAndVerifyOrgMembership(orgId, user)

                request.setAttribute(ORG_MEMBERSHIP_ATTRIBUTE, orgMembership)

                if (requiresUserInClassroom) {
                    val classroomNumber = pathVars[CLASSROOM_PARAM]?.toIntOrNull() ?:
                        throw InvalidInputException("Invalid classroom number")

                    val userClassroom =
                        usersDb.getUserMembershipInClassroom(orgId, classroomNumber, user.uid)

                    if (userClassroom.role == UserClassroomMembership.NOT_A_MEMBER)
                        throw AuthorizationException("User is not in classroom")

                    request.setAttribute(CLASSROOM_MEMBERSHIP_ATTRIBUTE, userClassroom)

                    if (requiresUserInAssignment) {
                        val assignmentNumber = pathVars[ASSIGNMENT_PARAM]?.toIntOrNull() ?:
                            throw InvalidInputException("Invalid assignment number")

                        val assignment = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber)
                        if (userClassroom.role == UserClassroomMembership.STUDENT) {
                            if (
                                assignment.isIndividualAssignment() && !usersDb.isUserInAssignment(assignment.aid, user.uid)
                                || assignment.isGroupAssignment() && !teamsDb.isUserTeamInAssignment(assignment.aid, user.uid)
                            ) throw AuthorizationException("User is not in assignment")
                        }

                        request.setAttribute(ASSIGNMENT_ATTRIBUTE, assignment)
                    }
                }
            }
        }

        return true
    }
}