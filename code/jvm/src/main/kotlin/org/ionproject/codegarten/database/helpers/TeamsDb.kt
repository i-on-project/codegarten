package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.CreatedTeam
import org.ionproject.codegarten.database.dto.DtoListWrapper
import org.ionproject.codegarten.database.dto.Team
import org.ionproject.codegarten.database.dto.TeamAssignment
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

private const val GET_TEAM_BASE =
    "SELECT tid, number, name, gh_id, org_id, classroom_id, classroom_number, classroom_name FROM V_TEAM"
private const val GET_TEAMS_BASE =
    "SELECT tid, number, name, gh_id, org_id, classroom_id, classroom_number, classroom_name, COUNT(*) OVER() as count FROM V_TEAM"

private const val GET_TEAMS_BY_CLASSROOM_ID_QUERY = "$GET_TEAMS_BASE WHERE classroom_id = :classroomId ORDER BY number"

private const val GET_TEAMS_BY_CLASSROOM_AND_USER_ID_QUERY =
    "$GET_TEAMS_BASE WHERE classroom_id = :classroomId AND tid IN (SELECT tid FROM USER_TEAM WHERE uid = :userId) ORDER BY number"

private const val GET_TEAM_BY_ID_QUERY = "$GET_TEAM_BASE WHERE tid = :teamId"
private const val GET_TEAM_BY_NUMBER_QUERY = "$GET_TEAM_BASE WHERE classroom_id = :classroomId AND number = :teamNumber"

private const val GET_USER_ID_OF_TEAM_QUERY = "SELECT uid FROM USER_TEAM WHERE tid = :teamId AND uid = :userId"

private const val CREATE_TEAM_QUERY = "INSERT INTO TEAM(cid, name, gh_id) VALUES(:classroomId, :name, :gitHubId)"

private const val UPDATE_TEAM_START = "UPDATE TEAM SET"
private const val UPDATE_TEAM_END = "WHERE tid = :teamId"

private const val DELETE_TEAM_QUERY = "DELETE FROM TEAM WHERE tid = :teamId"

// Assignments
private const val GET_TEAM_IN_ASSIGNMENT_BASE =
    "SELECT tid, number, name, gh_id, repo_id, assignment_id FROM V_TEAM_ASSIGNMENT"
private const val GET_TEAMS_IN_ASSIGNMENT_BASE =
    "SELECT tid, number, name, gh_id, repo_id, assignment_id, COUNT(*) OVER() as count FROM V_TEAM_ASSIGNMENT"

private const val GET_TEAMS_IN_ASSIGNMENT_QUERY =
    "$GET_TEAMS_IN_ASSIGNMENT_BASE WHERE assignment_id = :assignmentId ORDER BY number"

private const val GET_TEAM_IN_ASSIGNMENT_QUERY =
    "$GET_TEAM_IN_ASSIGNMENT_BASE WHERE assignment_id = :assignmentId AND tid = :teamId"

private const val GET_TEAM_ID_FROM_USER_IN_ASSIGNMENT_QUERY =
    "SELECT tid FROM V_TEAM_USER_ASSIGNMENT WHERE aid = :assignmentId AND uid = :userId"

private const val GET_TEAM_FROM_USER_IN_ASSIGNMENT_QUERY =
    "$GET_TEAM_BASE WHERE tid IN ($GET_TEAM_ID_FROM_USER_IN_ASSIGNMENT_QUERY)"

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

    fun getTeamsOfClassroom(orgId: Int, classroomNumber: Int, page: Int, limit: Int): DtoListWrapper<Team> {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        return getTeamsOfClassroom(classroomId, page, limit)
    }

    fun getTeamsOfClassroomOfUser(orgId: Int, classroomNumber: Int, userId: Int, page: Int, limit: Int): DtoListWrapper<Team> {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        return getTeamsOfClassroomOfUser(classroomId, userId, page, limit)
    }

    fun getTeamsOfClassroom(classroomId: Int, page: Int, limit: Int): DtoListWrapper<Team> {
        val results = jdbi.getList(
            GET_TEAMS_BY_CLASSROOM_ID_QUERY, Team::class.java,
            page, limit,
            mapOf("classroomId" to classroomId)
        )

        return DtoListWrapper(
            count = if (results.isEmpty()) 0 else results[0].count!!,
            results = results
        )
    }

    fun getTeamsOfClassroomOfUser(classroomId: Int, userId: Int, page: Int, limit: Int): DtoListWrapper<Team> {
        val results = jdbi.getList(
            GET_TEAMS_BY_CLASSROOM_AND_USER_ID_QUERY, Team::class.java,
            page, limit,
            mapOf(
                "classroomId" to classroomId,
                "userId" to userId
            )
        )

        return DtoListWrapper(
            count = if (results.isEmpty()) 0 else results[0].count!!,
            results = results
        )
    }

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

    fun getTeam(teamId: Int) =
        jdbi.getOne(
            GET_TEAM_BY_ID_QUERY,
            Team::class.java,
            mapOf(
                "teamId" to teamId
            )
        )

    fun tryGetTeam(teamId: Int) =
        jdbi.tryGetOne(
            GET_TEAM_BY_ID_QUERY,
            Team::class.java,
            mapOf(
                "teamId" to teamId
            )
        )

    fun createTeam(orgId: Int, classroomNumber: Int, name: String, gitHubId: Int): CreatedTeam {
        val classroomId = classroomsDb.getClassroomByNumber(orgId, classroomNumber).cid
        return createTeam(classroomId, name, gitHubId)
    }

    fun createTeam(classroomId: Int, name: String, gitHubId: Int) =
        jdbi.insertAndGet(
            CREATE_TEAM_QUERY, CreatedTeam::class.java,
            mapOf(
                "classroomId" to classroomId,
                "name" to name,
                "gitHubId" to gitHubId
            )
        )

    fun isUserInTeam(teamId: Int, userId: Int) =
        jdbi.tryGetOne(
            GET_USER_ID_OF_TEAM_QUERY, Int::class.java,
            mapOf(
                "teamId" to teamId,
                "userId" to userId
            )
        ).isPresent

    fun isUserTeamInAssignment(assignmentId: Int, userId: Int) =
        jdbi.tryGetOne(
            GET_TEAM_ID_FROM_USER_IN_ASSIGNMENT_QUERY,
            Int::class.java,
            mapOf(
                "assignmentId" to assignmentId,
                "userId" to userId,
            )
        ).isPresent

    fun getUserTeamInAssignment(assignmentId: Int, userId: Int) =
        jdbi.getOne(
            GET_TEAM_FROM_USER_IN_ASSIGNMENT_QUERY,
            Team::class.java,
            mapOf(
                "assignmentId" to assignmentId,
                "userId" to userId,
            )
        )

    fun tryGetUserTeamInAssignment(assignmentId: Int, userId: Int) =
        jdbi.tryGetOne(
            GET_TEAM_FROM_USER_IN_ASSIGNMENT_QUERY,
            Team::class.java,
            mapOf(
                "assignmentId" to assignmentId,
                "userId" to userId,
            )
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

    fun getTeamsFromAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, page: Int, limit: Int): DtoListWrapper<TeamAssignment> {
        val assignmentId = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid
        return getTeamsFromAssignment(assignmentId, page, limit)
    }

    fun getTeamsFromAssignment(assignmentId: Int, page: Int, limit: Int): DtoListWrapper<TeamAssignment> {
        val results = jdbi.getList(
            GET_TEAMS_IN_ASSIGNMENT_QUERY,
            TeamAssignment::class.java, page, limit,
            mapOf("assignmentId" to assignmentId)
        )

        return DtoListWrapper(
            count = if (results.isEmpty()) 0 else results[0].count!!,
            results = results
        )
    }

    fun getTeamAssignment(orgId: Int, classroomNumber: Int, assignmentNumber: Int, teamNumber: Int): TeamAssignment {
        val assignmentId = assignmentsDb.getAssignmentByNumber(orgId, classroomNumber, assignmentNumber).aid
        val teamId = getTeam(orgId, classroomNumber, teamNumber).tid
        return getTeamAssignment(assignmentId, teamId)
    }

    fun getTeamAssignment(assignmentId: Int, teamId: Int) =
        jdbi.getOne(
            GET_TEAM_IN_ASSIGNMENT_QUERY,
            TeamAssignment::class.java,
            mapOf(
                "assignmentId" to assignmentId,
                "teamId" to teamId
            )
        )

    fun tryGetTeamAssignment(assignmentId: Int, teamId: Int) =
        jdbi.tryGetOne(
            GET_TEAM_IN_ASSIGNMENT_QUERY,
            TeamAssignment::class.java,
            mapOf(
                "assignmentId" to assignmentId,
                "teamId" to teamId
            )
        )

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