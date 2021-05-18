package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.AccessToken
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

private const val GET_ACCESSTOKENS_BASE = "SELECT token, expiration_date, user_id, client_id FROM ACCESSTOKEN"
private const val GET_ACCESSTOKEN_QUERY = "$GET_ACCESSTOKENS_BASE WHERE token = :token"

private const val CREATE_ACCESSTOKEN_QUERY = "INSERT INTO ACCESSTOKEN VALUES(:token, :exp, :userId, :clientId)"

private const val DELETE_ACCESSTOKEN_QUERY = "DELETE FROM ACCESSTOKEN WHERE token = :token"

@Component
class AccessTokensDb(val jdbi: Jdbi) {

    fun getAccessToken(token: String) =
        jdbi.getOne(GET_ACCESSTOKEN_QUERY, AccessToken::class.java, mapOf("token" to token))

    fun createAccessToken(token: String, expirationDate: OffsetDateTime, userId: Int, clientId: Int) =
        jdbi.insert(
            CREATE_ACCESSTOKEN_QUERY,
            mapOf(
                "token" to token,
                "exp" to expirationDate,
                "userId" to userId,
                "clientId" to clientId
            )
        )

    fun deleteAccessToken(token: String) {
        jdbi.delete(DELETE_ACCESSTOKEN_QUERY, mapOf("token" to token))
    }
}