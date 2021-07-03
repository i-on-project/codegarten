package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.controllers.ControllerTester
import org.ionproject.codegarten.responses.ProblemJson
import org.ionproject.codegarten.responses.siren.Siren
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URI

class OrgsControllerTests : ControllerTester() {
    @Test
    fun testGetOrganizations() {
        val body = doGet(URI(Routes.ORGS_HREF)) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isOk() }
                content { contentType("application/vnd.siren+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val siren = mapper.readValue(body, Siren::class.java) as Siren<LinkedHashMap<String, Int>>

        assertEquals(0, siren.properties!!["pageIndex"])
        assertEquals(2, siren.properties!!["pageSize"])

        val entities = siren.entities as List<LinkedHashMap<String, LinkedHashMap<String, String>>>

        assertEquals(1, entities[0]["properties"]!!["id"])
        assertEquals("org1", entities[0]["properties"]!!["name"])

        assertEquals(2, entities[1]["properties"]!!["id"])
        assertEquals("org2", entities[1]["properties"]!!["name"])
    }

    @Test
    fun testSearchOrganizationRepositories() {
        val body = doGet(URI("${Routes.searchOrgRepositories(1)}?${Routes.SEARCH_PARAM}=repo")) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isOk() }
                content { contentType("application/vnd.siren+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val siren = mapper.readValue(body, Siren::class.java) as Siren<LinkedHashMap<String, Int>>
        assertEquals(2, siren.properties!!["collectionSize"])

        val entities = siren.entities as List<LinkedHashMap<String, LinkedHashMap<String, String>>>

        assertEquals(1, entities[0]["properties"]!!["id"])
        assertEquals("repo1", entities[0]["properties"]!!["name"])

        assertEquals(2, entities[1]["properties"]!!["id"])
        assertEquals("repo2", entities[1]["properties"]!!["name"])
    }

    @Test
    fun testSearchOrganizationRepositoriesEmptyResults() {
        val body = doGet(URI("${Routes.searchOrgRepositories(1)}?${Routes.SEARCH_PARAM}=test")) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isOk() }
                content { contentType("application/vnd.siren+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val siren = mapper.readValue(body, Siren::class.java) as Siren<LinkedHashMap<String, Int>>
        assertEquals(0, siren.properties!!["collectionSize"])
    }

    @Test
    fun testSearchOrganizationRepositoriesNotInOrg() {
        val body = doGet(URI("${Routes.searchOrgRepositories(1)}?${Routes.SEARCH_PARAM}=test")) {
            header("Authorization", "Bearer token3")
        }
            .andExpect {
                status { isForbidden() }
                content { contentType("application/problem+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val problemJson = mapper.readValue(body, ProblemJson::class.java)
        assertEquals("/problems/forbidden-operation", problemJson.type)
    }

    @Test
    fun testGetOrganization() {
        val body = doGet(Routes.getOrgByIdUri(1)) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isOk() }
                content { contentType("application/vnd.siren+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val siren = mapper.readValue(body, Siren::class.java) as Siren<LinkedHashMap<String, Int>>
        assertEquals(1, siren.properties!!["id"])
    }

    @Test
    fun testGetNonExistentOrganization() {
        val body = doGet(Routes.getOrgByIdUri(3)) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isNotFound() }
                content { contentType("application/problem+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val problemJson = mapper.readValue(body, ProblemJson::class.java)
        assertEquals("/problems/resource-not-found", problemJson.type)
    }
}