package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.AccessToken
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

private const val GET_ACESSTOKENS_BASE = "SELECT token, expiration_date, user_id, client_id FROM ACESSTOKEN"
private const val GET_ACESSTOKEN_QUERY = "$GET_ACESSTOKENS_BASE WHERE token = :token"

@Component
class AccessTokensDb(val jdbi: Jdbi) {

    fun getAccessToken(token: String) =
        jdbi.getOne(GET_ACESSTOKEN_QUERY, AccessToken::class.java, mapOf("token" to token))
}