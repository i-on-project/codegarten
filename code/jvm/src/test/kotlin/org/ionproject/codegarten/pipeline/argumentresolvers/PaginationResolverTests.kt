package org.ionproject.codegarten.pipeline.argumentresolvers

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.controllers.ControllerTester
import org.ionproject.codegarten.responses.ProblemJson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.net.URI

class PaginationResolverTests : ControllerTester() {
    @Test
    fun testInvalidPage() {
        val body = doGet(URI("${Routes.ORGS_HREF}?${Routes.PAGE_PARAM}=-1")) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isBadRequest() }
                content { contentType("application/problem+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val problemJson = mapper.readValue(body, ProblemJson::class.java)
        Assertions.assertEquals("/problems/invalid-pagination-parameters", problemJson.type)
        Assertions.assertEquals(Routes.ORGS_HREF, problemJson.instance)
    }

    @Test
    fun testInvalidLimit() {
        val body = doGet(URI("${Routes.ORGS_HREF}?${Routes.LIMIT_PARAM}=-1")) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isBadRequest() }
                content { contentType("application/problem+json") }
            }
            .andReturn()
            .response
            .contentAsString
        val problemJson = mapper.readValue(body, ProblemJson::class.java)
        Assertions.assertEquals("/problems/invalid-pagination-parameters", problemJson.type)
        Assertions.assertEquals(Routes.ORGS_HREF, problemJson.instance)
    }

    @Test
    fun testValidPageButInvalidLimit() {
        val body = doGet(URI("${Routes.ORGS_HREF}?${Routes.PAGE_PARAM}=0&${Routes.LIMIT_PARAM}=-1")) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isBadRequest() }
                content { contentType("application/problem+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val problemJson = mapper.readValue(body, ProblemJson::class.java)
        Assertions.assertEquals("/problems/invalid-pagination-parameters", problemJson.type)
        Assertions.assertEquals(Routes.ORGS_HREF, problemJson.instance)
    }

    @Test
    fun testValidLimitButInvalidPage() {
        val body = doGet(URI("${Routes.ORGS_HREF}?${Routes.PAGE_PARAM}=-1&${Routes.LIMIT_PARAM}=2")) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isBadRequest() }
                content { contentType("application/problem+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val problemJson = mapper.readValue(body, ProblemJson::class.java)
        Assertions.assertEquals("/problems/invalid-pagination-parameters", problemJson.type)
        Assertions.assertEquals(Routes.ORGS_HREF, problemJson.instance)
    }


    @Test
    fun testValidPage() {
        doGet(URI("${Routes.ORGS_HREF}?${Routes.PAGE_PARAM}=1")) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isOk() }
            }
    }
}