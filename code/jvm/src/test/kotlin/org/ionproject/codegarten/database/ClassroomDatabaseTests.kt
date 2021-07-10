package org.ionproject.codegarten.database

import org.ionproject.codegarten.database.helpers.ClassroomsDb
import org.ionproject.codegarten.exceptions.NotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@ExtendWith(DatabaseInitializer::class)
class ClassroomDatabaseTests {

    @Autowired
    lateinit var db: ClassroomsDb

    private val logger = LoggerFactory.getLogger(ClassroomDatabaseTests::class.java)

    @Test
    fun testGetClassroom() {
        logger.info("Testing get classroom")
        val classroom = db.getClassroomById(1)
        assertEquals(1, classroom.cid)
        assertEquals(1, classroom.org_id)
        assertEquals(1, classroom.number)
        assertEquals("Classroom 1", classroom.name)
        assertEquals("Description of Classroom 1", classroom.description)
        assertEquals("inv1", classroom.inv_code)
    }

    @Test
    fun testGetNonExistentClassroom() {
        logger.info("Testing get non existent classroom")
        assertThrows<NotFoundException> {
            db.getClassroomById(0)
        }
    }

    @Test
    fun testCreateClassroom() {
        logger.info("Testing create and get classroom")

        val orgId = 1
        val name = "New Classroom"
        val description = "New Classroom Description"

        val classroom = db.createClassroom(orgId, name, description)

        assertEquals(orgId, classroom.org_id)
        assertEquals(name, classroom.name)
        assertEquals(description, classroom.description)
    }

    @Test
    fun testCreateAndDeleteClassroom() {
        logger.info("Testing create and delete classroom")

        val toDelete = db.createClassroom(1, "To Delete", "To Delete")
        db.deleteClassroom(toDelete.org_id, toDelete.number)

        assertThrows<NotFoundException> {
            db.getClassroomById(toDelete.cid)
        }
    }

    @Test
    fun testDeleteNonExistentClassroom() {
        logger.info("Testing delete non existent classroom")
        assertThrows<NotFoundException> {
            db.deleteClassroom(1, 0)
        }
    }

    @Test
    fun testCreateAndEditClassroom() {
        logger.info("Testing create and edit classroom")

        val orgId = 1
        val editedName = "Edited Name"
        val editDescription = "Edited Description"

        val toEdit = db.createClassroom(orgId, "To edit", "To edit")
        db.editClassroom(toEdit.org_id, toEdit.number, editedName, editDescription)
        val classroom = db.getClassroomById(toEdit.cid)

        assertEquals(orgId, classroom.org_id)
        assertEquals(editedName, classroom.name)
        assertEquals(editDescription, classroom.description)
    }

    @Test
    fun testEditNonExistentClassroom() {
        logger.info("Testing edit non existent classroom")
        assertThrows<NotFoundException> {
            db.editClassroom(1, 0, "fail", "fail")
        }
    }
}