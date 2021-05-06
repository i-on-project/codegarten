package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.Team
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

private const val GET_TEAMS_BASE =
    "SELECT tid, number, name, gh_id, org_id, classroom_id, classroom_number, classroom_name FROM V_TEAM"
private const val GET_TEAMS_BY_CLASSROOM_ID_QUERY = "$GET_TEAMS_BASE WHERE classroom_id = :classroomId"
private const val GET_TEAMS_BY_CLASSROOM_ID_COUNT = "SELECT COUNT(tid) FROM TEAM WHERE cid = :classroomId"

private const val GET_TEAM_BY_ID_QUERY = "$GET_TEAMS_BASE WHERE tid = :teamId"
private const val GET_TEAM_BY_NUMBER_QUERY = "$GET_TEAMS_BASE WHERE classroom_id = :classroomId AND number = :teamNumber"

private const val CREATE_TEAM_QUERY = "INSERT INTO TEAM(cid, name, gh_id) VALUES(:classroomId, :name, :gitHubId)"

private const val UPDATE_TEAM_START = "UPDATE TEAM SET"
private const val UPDATE_TEAM_END = "WHERE tid = :teamId"

private const val DELETE_TEAM_QUERY = "DELETE FROM TEAM WHERE tid = :teamId"

// Assignments

private const val ADD_TEAM_TO_ASSIGNMENT =
    "INSERT INTO TEAM_ASSIGNMENT(tid, aid, repo_id) VALUES(:teamId, :assignmentId, :repoId)"

private const val DELETE_TEAM_FROM_ASSIGNMENT =
    "DELETE FROM TEAM_ASSIGNMENT WHERE aid = :assignmentId AND tid = :teamId"

@Component
class TeamsDb(
    val jdbi: Jdbi,
    val classroomsDb: ClassroomsDb,
    val assignmentsDb: AssignmentsDb
) {

    fun getTeamsOfClassroom(orgId: Int, classroomNumber: Int, page: Int, limit: Int): List<Team> {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        return getTeamsOfClassroom(classroomId, page, limit)
    }

    fun getTeamsOfClassroomCount(orgId: Int, classroomNumber: Int): Int {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        return getTeamsOfClassroomCount(classroomId)
    }

    fun getTeamsOfClassroom(classroomId: Int, page: Int, limit: Int): List<Team> =
        jdbi.getList(
            GET_TEAMS_BY_CLASSROOM_ID_QUERY, Team::class.java,
            page, limit,
            mapOf("classroomId" to classroomId)
        )

    fun getTeamsOfClassroomCount(classroomId: Int): Int =
        jdbi.getOne(
            GET_TEAMS_BY_CLASSROOM_ID_COUNT,
            Int::class.java,
            mapOf("classroomId" to classroomId)
        )

    fun getTeam(orgId: Int, classroomNumber: Int, teamNumber: Int): Team {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        return getTeam(classroomId, teamNumber)
    }

    fun getTeam(classroomId: Int, teamNumber: Int) =
        jdbi.getOne(
            GET_TEAM_BY_NUMBER_QUERY,
            Team::class.java,
            mapOf(
                "classroomId" to classroomId,
                "teamNumber" to teamNumber
            )
        )

    fun getTeam(teamId: Int): Team =
        jdbi.getOne(
            GET_TEAM_BY_ID_QUERY,
            Team::class.java,
            mapOf(
                "teamId" to teamId
            )
        )

    fun createTeam(orgId: Int, classroomNumber: Int, name: String, gitHubId: Int): Team {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        return createTeam(classroomId, name, gitHubId)
    }

    fun createTeam(classroomId: Int, name: String, gitHubId: Int) =
        jdbi.insertAndGet(
            CREATE_TEAM_QUERY, Int::class.java,
            GET_TEAM_BY_ID_QUERY, Team::class.java,
            mapOf(
                "classroomId" to classroomId,
                "name" to name,
                "gitHubId" to gitHubId
            ),
            "teamId"
        )

    fun editTeam(orgId: Int, classroomNumber: Int, teamNumber: Int, name: String) {
        val teamId = getTeam(orgId, classroomNumber, teamNumber).tid
        editTeam(teamId, name)
    }

    fun editTeam(classroomId: Int, teamNumber: Int, name: String) {
        val teamId = getTeam(classroomId, teamNumber).tid
        editTeam(teamId, name)
    }

    fun editTeam(teamId: Int, name: String) =
        jdbi.update(
            UPDATE_TEAM_START,
            mapOf("name" to name),
            UPDATE_TEAM_END,
            mapOf("teamId" to teamId)
        )

    fun deleteTeam(orgId: Int, classroomNumber: Int, teamNumber: Int) {
        val teamId = getTeam(orgId, classroomNumber, teamNumber).tid
        deleteTeam(teamId)
    }

    fun deleteTeam(classroomId: Int, teamNumber: Int) {
        val teamId = getTeam(classroomId, teamNumber).tid
        deleteTeam(teamId)
    }

    fun deleteTeam(teamId: Int) =
        jdbi.delete(
            DELETE_TEAM_QUERY,
            mapOf("teamId" to teamId)
        )

    // Assignments

    fun addTeamToAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, teamNumber: Int, repoId: Int) {
        val teamId = getTeam(orgId, classroomNumber, teamNumber).tid
        val assignmentId = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid
        addTeamToAssignment(assignmentId, teamId, repoId)
    }

    fun addTeamToAssignment(assignmentId: Int, teamId: Int, repoId: Int) =
        jdbi.insert(
            ADD_TEAM_TO_ASSIGNMENT,
            mapOf(
                "assignmentId" to assignmentId,
                "teamId" to teamId,
                "repoId" to repoId
            )
        )

    fun deleteTeamFromAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, teamNumber: Int) {
        val teamId = getTeam(orgId, classroomNumber, teamNumber).tid
        val assignmentId = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid
        deleteTeamFromAssignment(assignmentId, teamId)
    }

    fun deleteTeamFromAssignment(assignmentId: Int, teamId: Int) =
        jdbi.delete(
            DELETE_TEAM_FROM_ASSIGNMENT,
            mapOf(
                "assignmentId" to assignmentId,
                "teamId" to teamId
            )
        )
}