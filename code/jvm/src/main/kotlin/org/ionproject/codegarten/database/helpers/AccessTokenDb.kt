package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dao.AuthCodeDao
import org.springframework.stereotype.Component

private const val GET_ACESSTOKENS_BASE = "SELECT token, expiration_date, user_id, client_id FROM ACESSTOKEN"
private const val GET_ACESSTOKEN_QUERY = "$GET_ACESSTOKENS_BASE WHERE token = :token"

@Component
class AccessTokenDb : DatabaseHelper() {
    fun getAccessToken(token: String) = getOne(GET_ACESSTOKEN_QUERY, AuthCodeDao::class.java, Pair("token", token))
}