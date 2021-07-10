package org.ionproject.codegarten.models

import com.fasterxml.jackson.databind.ObjectMapper
import org.ionproject.codegarten.Routes
import org.ionproject.codegarten.controllers.api.actions.AssignmentActions
import org.ionproject.codegarten.controllers.api.actions.ClassroomActions
import org.ionproject.codegarten.controllers.models.AssignmentItemOutputModel
import org.ionproject.codegarten.controllers.models.AssignmentsOutputModel
import org.ionproject.codegarten.controllers.models.ClassroomOutputModel
import org.ionproject.codegarten.database.dto.Assignment
import org.ionproject.codegarten.database.dto.Classroom
import org.ionproject.codegarten.database.dto.DtoListWrapper
import org.ionproject.codegarten.remote.github.GitHubRoutes
import org.ionproject.codegarten.responses.siren.SirenLink
import org.ionproject.codegarten.testutils.TestUtils.getResourceFile
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

const val EXPECTED_ASSIGNMENTS_PATH = "expectedJson/assignments.json"
const val EXPECTED_CLASSROOM_PATH = "expectedJson/classroom.json"

@SpringBootTest
class SirenModelTest {

    @Autowired
    lateinit var mapper: ObjectMapper

    private val logger = LoggerFactory.getLogger(SirenModelTest::class.java)

    @Test
    fun testSirenMappingItem() {
        logger.info("Testing Siren mapping to an item")

        val orgId = 1
        val classroom = Classroom(
            1, 1, "inv", orgId, "classroom", "desc"
        )

        val actions = listOf(
            ClassroomActions.getEditClassroomAction(orgId, classroom.number),
            ClassroomActions.getDeleteClassroomAction(orgId, classroom.number)
        )

        val classroomSirenModel = ClassroomOutputModel(
            id = classroom.cid,
            inviteCode = classroom.inv_code,
            number = classroom.number,
            name = classroom.name,
            description = classroom.description,
            organization = "organization"
        ).toSirenObject(
            actions = actions,
            links = listOf(
                SirenLink(listOf(Routes.SELF_PARAM), Routes.getClassroomByNumberUri(orgId, classroom.number)),
                SirenLink(listOf("assignments"), Routes.getAssignmentsUri(orgId, classroom.number)),
                SirenLink(listOf("teams"), Routes.getTeamsUri(orgId, classroom.number)),
                SirenLink(listOf("users"), Routes.getUsersOfClassroomUri(orgId, classroom.number)),
                SirenLink(listOf("classrooms"), Routes.getClassroomsUri(orgId)),
                SirenLink(listOf("organization"), Routes.getOrgByIdUri(orgId)),
                SirenLink(listOf("organizationGitHub"), GitHubRoutes.getGithubLoginUri("organization"))
            )
        )

        assertEquals(listOf("classroom"), classroomSirenModel.clazz.map { it.name })
        assertEquals(actions, classroomSirenModel.actions)
        assertTrue(classroomSirenModel.links.isNotEmpty())

        val actual = JSONObject(mapper.writeValueAsString(classroomSirenModel))
        val expected = JSONObject(getResourceFile(EXPECTED_CLASSROOM_PATH).readText())

        assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun testSirenMappingList() {
        logger.info("Testing Siren mapping to a list")

        val orgId = 1
        val classroomId = 1
        val classroomNumber = 1
        val actions = listOf(
            AssignmentActions.getCreateAssignmentAction(orgId, classroomNumber)
        )

        val assignments = DtoListWrapper(
            count = 3,
            listOf(
                Assignment(
                    1, 1, "inv1", "assignment 1", "desc 1", "individual",
                    "prefix", 1, orgId, classroomId, classroomNumber, "classroom"
                ),
                Assignment(
                    2, 2, "inv2", "assignment 2", "desc 2", "individual",
                    "prefix", 2, orgId, classroomId, classroomNumber, "classroom"
                ),
                Assignment(
                    3, 3, "inv3", "assignment 3", "desc 3", "individual",
                    "prefix", 3, orgId, classroomId, classroomNumber, "classroom"
                )
            )
        )

        val assignmentsSirenModel = AssignmentsOutputModel(
            classroom = "classroom",
            organization = "organization",
            collectionSize = 3,
            pageIndex = 0,
            pageSize = 3,
        ).toSirenObject(
            entities = assignments.results.map {
                AssignmentItemOutputModel(
                    id = it.aid,
                    inviteCode = it.inv_code,
                    number = it.number,
                    name = it.name,
                    description = it.description,
                    type = it.type,
                    classroom = it.classroom_name,
                    organization = "organization"
                ).toSirenObject(
                    rel = listOf("item"),
                    links = listOf(
                        SirenLink(listOf(Routes.SELF_PARAM), Routes.getAssignmentByNumberUri(orgId, classroomNumber, it.number)),
                        SirenLink(listOf("deliveries"), Routes.getDeliveriesUri(orgId, classroomNumber, it.number)),
                        SirenLink(listOf("participants"), Routes.getParticipantsOfAssignmentUri(orgId, classroomNumber, it.number)),
                        SirenLink(listOf("assignments"), Routes.getAssignmentsUri(orgId, classroomNumber)),
                        SirenLink(listOf("classroom"), Routes.getClassroomByNumberUri(orgId, classroomNumber)),
                        SirenLink(listOf("organization"), Routes.getOrgByIdUri(orgId)),
                        SirenLink(listOf("organizationGitHub"), GitHubRoutes.getGithubLoginUri("organization"))
                    )
                )
            },
            actions = actions,
            links = Routes.createSirenLinkListForPagination(
                Routes.getAssignmentsUri(orgId, classroomNumber),
                0,
                10,
                assignments.count
            ) + listOf(
                SirenLink(listOf("classroom"), Routes.getClassroomByNumberUri(orgId, classroomNumber)),
                SirenLink(listOf("organization"), Routes.getOrgByIdUri(orgId)),
                SirenLink(listOf("organizationGitHub"), GitHubRoutes.getGithubLoginUri("organization"))
            )
        )

        assertEquals(listOf("assignment", "collection"), assignmentsSirenModel.clazz.map { it.name })
        assertEquals(actions, assignmentsSirenModel.actions)
        assertEquals(3, assignmentsSirenModel.entities!!.size)
        assertTrue(assignmentsSirenModel.links.isNotEmpty())

        val actual = JSONObject(mapper.writeValueAsString(assignmentsSirenModel))
        val expected = JSONObject(getResourceFile(EXPECTED_ASSIGNMENTS_PATH).readText())

        assertEquals(expected.toString(), actual.toString())
    }
}