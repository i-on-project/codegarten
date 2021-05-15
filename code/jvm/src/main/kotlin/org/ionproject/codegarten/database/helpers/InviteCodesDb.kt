package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.PsqlErrorCode
import org.ionproject.codegarten.database.dto.InviteCode
import org.ionproject.codegarten.database.getPsqlErrorCode
import org.ionproject.codegarten.utils.CryptoUtils
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.JdbiException
import org.springframework.stereotype.Component

private const val GET_INVITE_CODES_BASE = "SELECT inv_code, type, assignment_id, classroom_id, org_id FROM V_INVITECODE"

private const val GET_INVITE_CODE_QUERY = "$GET_INVITE_CODES_BASE WHERE inv_code = :invCode"

private const val CREATE_INVITE_CODE_QUERY =
    "INSERT INTO INVITECODE(inv_code, type, aid, cid) VALUES(:invCode, :type, :assignmentId, :classroomId)"

@Component
class InviteCodesDb(
    val jdbi: Jdbi,
    val cryptoUtils: CryptoUtils,
) {

    fun getInviteCode(invCode: String) =
        jdbi.getOne(
            GET_INVITE_CODE_QUERY,
            InviteCode::class.java,
            mapOf("invCode" to invCode)
        )

    fun createInviteCode(invCode: String, classroomId: Int, assignmentId: Int? = null) {
        // If assignmentId is null, the invite code is for a classroom
        val type = if (assignmentId == null) "classroom" else "assignment"

        jdbi.insert(
            CREATE_INVITE_CODE_QUERY,
            mapOf(
                "invCode" to invCode,
                "type" to type,
                "assignmentId" to assignmentId,
                "classroomId" to classroomId
            )
        )
    }

    fun generateAndCreateUniqueInviteCode(classroomId: Int, assignmentId: Int? = null) {
        while (true) {
            val invCode = cryptoUtils.generateInviteCode()
            try {
                createInviteCode(invCode, classroomId, assignmentId)
                break
            } catch (ex: JdbiException) {
                if (ex.getPsqlErrorCode() != PsqlErrorCode.UniqueViolation) throw ex
                // If invite code was not unique, the loop will repeat and generate a new one
            }
        }
    }
}