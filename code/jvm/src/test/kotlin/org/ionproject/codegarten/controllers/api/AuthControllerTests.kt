package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.controllers.ControllerTester
import org.ionproject.codegarten.responses.AccessToken
import org.ionproject.codegarten.responses.ProblemJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.net.URI

class AuthControllerTests : ControllerTester() {

    @Test
    fun testGetAccessToken() {
        val body = doPost(URI(Routes.AUTH_TOKEN_HREF)) {
            contentType = MediaType.APPLICATION_FORM_URLENCODED

            content = "code=code&client_id=1&client_secret=test-secret"
        }
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
            }
            .andReturn()
            .response
            .contentAsString

        val accessToken = mapper.readValue(body, AccessToken::class.java)

        assertNotNull(accessToken.access_token)
        assertNotNull(accessToken.expires_in)
    }

    @Test
    fun testGetAccessTokenInvalidCode() {
        val body = doPost(URI(Routes.AUTH_TOKEN_HREF)) {
            contentType = MediaType.APPLICATION_FORM_URLENCODED

            content = "code=thiscodedoesnotexist&client_id=1&client_secret=test-secret"
        }
            .andExpect {
                status { isUnauthorized() }
                content { contentType("application/problem+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val problemJson = mapper.readValue(body, ProblemJson::class.java)
        assertEquals("/problems/not-authorized", problemJson.type)
    }

    @Test
    fun testGetAccessTokenInvalidClient() {
        val body = doPost(URI(Routes.AUTH_TOKEN_HREF)) {
            contentType = MediaType.APPLICATION_FORM_URLENCODED

            content = "code=code&client_id=1&client_secret=invalid-secret"
        }
            .andExpect {
                status { isUnauthorized() }
                content { contentType("application/problem+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val problemJson = mapper.readValue(body, ProblemJson::class.java)
        assertEquals("/problems/invalid-client", problemJson.type)
    }

    @Test
    fun testRevokeAccessToken() {
        doPost(URI(Routes.AUTH_REVOKE_HREF)) {
            contentType = MediaType.APPLICATION_FORM_URLENCODED

            content = "token=tokentorevoke&client_id=1&client_secret=test-secret"
        }
            .andExpect {
                status { isOk() }
            }

        doGet(URI(Routes.ORGS_HREF)) {
            header("Authorization", "Bearer tokentorevoke")
        }
            .andExpect {
                status { isUnauthorized() }
                content { contentType("application/problem+json") }
            }
    }

    @Test
    fun testRevokeNonExistentToken() {
        doPost(URI(Routes.AUTH_REVOKE_HREF)) {
            contentType = MediaType.APPLICATION_FORM_URLENCODED

            content = "token=thistokendoesnotexist&client_id=1&client_secret=test-secret"
        }
            .andExpect {
                status { isUnauthorized() }
                content { contentType("application/problem+json") }
            }
    }
}