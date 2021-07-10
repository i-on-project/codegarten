package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.controllers.ControllerTester
import org.ionproject.codegarten.controllers.models.UserInvitationInputModel
import org.ionproject.codegarten.utils.toJson
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class ParticipantsControllerTests : ControllerTester() {

    @Test
    fun testJoinIndividualAssignmentThroughInvite() {
        doPut(Routes.getUserInviteUri("inv3")) {
            header("Authorization", "Bearer token2")
        }
            .andExpect {
                status { isCreated() }
                content { contentType("application/vnd.siren+json") }
                header { exists("Location") }
            }
    }

    @Test
    fun testJoinTeamAssignmentThroughInvite() {
        doPut(Routes.getUserInviteUri("inv4")) {
            header("Authorization", "Bearer token2")
            contentType = MediaType.APPLICATION_JSON

            content = UserInvitationInputModel(1).toJson(mapper)
        }
            .andExpect {
                status { isCreated() }
                content { contentType("application/vnd.siren+json") }
                header { exists("Location") }
            }
    }

    @Test
    fun testJoinInvalidInvite() {
        doPut(Routes.getUserInviteUri("inv0")) {
            header("Authorization", "Bearer token2")
        }
            .andExpect {
                status { isNotFound() }
                content { contentType("application/problem+json") }
            }
    }

    @Test
    fun testJoinInviteNonExistentTeam() {
        doPut(Routes.getUserInviteUri("inv4")) {
            header("Authorization", "Bearer token2")

            contentType = MediaType.APPLICATION_JSON
            content = UserInvitationInputModel(0).toJson(mapper)
        }
            .andExpect {
                status { isBadRequest() }
                content { contentType("application/problem+json") }
            }
    }
}