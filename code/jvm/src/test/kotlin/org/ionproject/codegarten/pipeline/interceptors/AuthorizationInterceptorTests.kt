package org.ionproject.codegarten.pipeline.interceptors

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.controllers.ControllerTester
import org.ionproject.codegarten.responses.ProblemJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URI

class AuthorizationInterceptorTests : ControllerTester() {
    @Test
    fun testAuthorizationNoHeader() {
        val body = doGet(URI(Routes.ORGS_HREF))
            .andExpect {
                status { isUnauthorized() }
            }
            .andReturn()
            .response
            .contentAsString

        val problemJson = mapper.readValue(body, ProblemJson::class.java)
        assertEquals("/problems/not-authorized", problemJson.type)
        assertEquals(Routes.ORGS_HREF, problemJson.instance)
    }

    @Test
    fun testAuthorizationWithInvalidAuthScheme() {
        val body = doGet(URI(Routes.ORGS_HREF)) {
            header("Authorization", "Basic token1")
        }
            .andExpect {
                status { isUnauthorized() }
            }
            .andReturn()
            .response
            .contentAsString

        val problemJson = mapper.readValue(body, ProblemJson::class.java)
        assertEquals("/problems/not-authorized", problemJson.type)
        assertEquals(Routes.ORGS_HREF, problemJson.instance)
    }

    @Test
    fun testAuthorizationWithInvalidToken() {
        val body = doGet(URI(Routes.ORGS_HREF)) {
            header("Authorization", "Bearer thistokendoesnotexist")
        }
            .andExpect {
                status { isUnauthorized() }
            }
            .andReturn()
            .response
            .contentAsString

        val problemJson = mapper.readValue(body, ProblemJson::class.java)
        assertEquals("/problems/not-authorized", problemJson.type)
        assertEquals(Routes.ORGS_HREF, problemJson.instance)
    }

    @Test
    fun testAuthorizationWithValidHeader() {
        doGet(URI(Routes.ORGS_HREF)) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isOk() }
            }
    }
}