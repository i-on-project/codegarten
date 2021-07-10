package org.ionproject.codegarten.controllers.api

import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.controllers.ControllerTester
import org.ionproject.codegarten.controllers.models.ClassroomCreateInputModel
import org.ionproject.codegarten.responses.ProblemJson
import org.ionproject.codegarten.responses.siren.Siren
import org.ionproject.codegarten.utils.toJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class ClassroomsControllerTests : ControllerTester() {
    @Test
    fun testGetClassrooms() {
        val body = doGet(Routes.getClassroomsUri(1)) {
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

        assertEquals(0, siren.properties!!["pageIndex"])
        assertTrue(siren.properties!!["pageSize"]!! as Int >= 2) // More classrooms may be added in other tests

        val entities = siren.entities as List<LinkedHashMap<String, LinkedHashMap<String, String>>>

        assertEquals(1, entities[0]["properties"]!!["id"])
        assertEquals("Classroom 1", entities[0]["properties"]!!["name"])

        assertEquals(2, entities[1]["properties"]!!["id"])
        assertEquals("Classroom 2", entities[1]["properties"]!!["name"])
    }

    @Test
    fun testGetClassroomsActionsTest() {
        val bodyTeacher = doGet(Routes.getClassroomsUri(1)) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isOk() }
                content { contentType("application/vnd.siren+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val sirenTeacher = mapper.readValue(bodyTeacher, Siren::class.java) as Siren<LinkedHashMap<String, Any>>

        assertFalse(sirenTeacher.actions!!.isEmpty())

        val bodyStudent = doGet(Routes.getClassroomsUri(1)) {
            header("Authorization", "Bearer token2")
        }
            .andExpect {
                status { isOk() }
                content { contentType("application/vnd.siren+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val sirenStudent = mapper.readValue(bodyStudent, Siren::class.java) as Siren<LinkedHashMap<String, Any>>

        assertNull(sirenStudent.actions)
    }

    @Test
    fun testGetClassroom() {
        val body = doGet(Routes.getClassroomByNumberUri(1, 1)) {
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
        assertEquals(1, siren.properties!!["id"])
        assertEquals(1, siren.properties!!["number"])
        assertEquals("Classroom 1", siren.properties!!["name"])
    }

    @Test
    fun testGetNonExistentClassroom() {
        val body = doGet(Routes.getClassroomByNumberUri(1, 0)) {
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

    @Test
    fun testGetClassroomActions() {
        val bodyTeacher = doGet(Routes.getClassroomByNumberUri(1, 1)) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isOk() }
                content { contentType("application/vnd.siren+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val sirenTeacher = mapper.readValue(bodyTeacher, Siren::class.java) as Siren<LinkedHashMap<String, Any>>

        assertFalse(sirenTeacher.actions!!.isEmpty())

        val bodyStudent = doGet(Routes.getClassroomByNumberUri(1, 1)) {
            header("Authorization", "Bearer token2")
        }
            .andExpect {
                status { isOk() }
                content { contentType("application/vnd.siren+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val sirenStudent = mapper.readValue(bodyStudent, Siren::class.java) as Siren<LinkedHashMap<String, Any>>

        assertNull(sirenStudent.actions)
    }

    @Test
    fun testGetClassroom_NotInClassroom() {
        val body = doGet(Routes.getClassroomByNumberUri(1, 1)) {
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
    fun testCreateClassroom() {
        val body = doPost(Routes.getClassroomsUri(1)) {
            header("Authorization", "Bearer token1")
            contentType = MediaType.APPLICATION_JSON

            content = ClassroomCreateInputModel("Created Classroom", "Description").toJson(mapper)
        }
            .andExpect {
                status { isCreated() }
                content { contentType("application/vnd.siren+json") }
                header { exists("Location") }
            }
            .andReturn()
            .response
            .contentAsString

        val siren = mapper.readValue(body, Siren::class.java) as Siren<LinkedHashMap<String, Any>>

        assertEquals("Created Classroom", siren.properties!!["name"])
        assertEquals("Description", siren.properties!!["description"])
    }

    @Test
    fun testCreateAlreadyExistentClassroom() {
        val body = doPost(Routes.getClassroomsUri(1)) {
            header("Authorization", "Bearer token1")
            contentType = MediaType.APPLICATION_JSON

            content = ClassroomCreateInputModel("Classroom 1", "Description").toJson(mapper)
        }
            .andExpect {
                status { isConflict() }
                content { contentType("application/problem+json") }
            }
            .andReturn()
            .response
            .contentAsString

        val problemJson = mapper.readValue(body, ProblemJson::class.java)
        assertEquals("/problems/resource-already-exists", problemJson.type)
    }

    @Test
    fun testEditClassroom() {
        doPut(Routes.getClassroomByNumberUri(1, 3)) {
            header("Authorization", "Bearer token1")
            contentType = MediaType.APPLICATION_JSON

            content = ClassroomCreateInputModel("I Was Edited", "Different Description").toJson(mapper)
        }
            .andExpect {
                status { isOk() }
                header { exists("Location") }
            }

        val body = doGet(Routes.getClassroomByNumberUri(1, 3)) {
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

        assertEquals("I Was Edited", siren.properties!!["name"])
        assertEquals("Different Description", siren.properties!!["description"])
    }

    @Test
    fun testDeleteClassroom() {
        doDelete(Routes.getClassroomByNumberUri(1, 4)) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun testDeleteNonExistentClassroom() {
        doDelete(Routes.getClassroomByNumberUri(1, 0)) {
            header("Authorization", "Bearer token1")
        }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun testDeleteClassroom_NoPermission() {
        val body = doDelete(Routes.getClassroomByNumberUri(1, 1)) {
            header("Authorization", "Bearer token2")
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
}