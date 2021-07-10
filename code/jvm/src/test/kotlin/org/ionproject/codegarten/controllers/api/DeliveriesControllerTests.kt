package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.controllers.ControllerTester
import org.ionproject.codegarten.responses.siren.Siren
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DeliveriesControllerTests : ControllerTester() {

    @Test
    fun testGetParticipantDeliveries() {
        val body = doGet(Routes.getDeliveriesOfParticipantUri(1, 1, 1, 4)) {
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
        val entities = siren.entities as List<LinkedHashMap<String, LinkedHashMap<String, Any>>>
        assertEquals(2, siren.properties!!["collectionSize"])
        assertTrue(entities[0]["properties"]!!["isDelivered"] as Boolean)
        assertFalse(entities[1]["properties"]!!["isDelivered"] as Boolean)
    }
}