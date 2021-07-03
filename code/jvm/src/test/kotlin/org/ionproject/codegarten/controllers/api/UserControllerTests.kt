package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.controllers.ControllerTester
import org.ionproject.codegarten.responses.siren.Siren
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URI

class UserControllerTests : ControllerTester() {

    @Test
    fun testGetAuthUser() {
        val body = doGet(URI(Routes.USER_HREF)) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isOk() }
                content { contentType("application/vnd.siren+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val siren = mapper.readValue(body, Siren::class.java) as Siren<LinkedHashMap<String, Any>>

        assertEquals("teacher", siren.properties!!["name"])
        assertEquals(1, siren.properties!!["id"])
    }
}