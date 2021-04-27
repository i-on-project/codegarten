package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.AuthCode
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

private const val GET_AUTHCODES_BASE = "SELECT code, expiration_date, user_id, client_id FROM AUTHCODE"
private const val GET_AUTHCODE_QUERY = "$GET_AUTHCODES_BASE WHERE code = :code"

private const val CREATE_AUTHCODE_QUERY = "INSERT INTO AUTHCODE VALUES(:code, :exp, :userId, :clientId)"

private const val DELETE_AUTHCODE_QUERY = "DELETE FROM AUTHCODE WHERE code = :code"

@Component
class AuthCodesDb(val jdbi: Jdbi) {

    fun getAuthCode(code: String) =
        jdbi.getOne(GET_AUTHCODE_QUERY, AuthCode::class.java, mapOf("code" to code))

    fun createAuthCode(code: String, expirationDate: OffsetDateTime, userId: Int, clientId: Int) =
        jdbi.insert(
            CREATE_AUTHCODE_QUERY,
            mapOf(
                "code" to code,
                "exp" to expirationDate,
                "userId" to userId,
                "clientId" to clientId
            )
        )

    fun deleteAuthCode(code: String) {
        jdbi.delete(
            DELETE_AUTHCODE_QUERY,
            mapOf(
                "code" to code
            )
        )
    }
}