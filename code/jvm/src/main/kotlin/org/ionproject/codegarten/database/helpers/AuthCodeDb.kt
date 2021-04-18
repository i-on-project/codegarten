package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dao.AuthCodeDao
import org.springframework.stereotype.Component

private const val GET_AUTHCODES_BASE = "SELECT code, expiration_date, user_id, client_id FROM AUTHCODE"
private const val GET_AUTHCODE_QUERY = "$GET_AUTHCODES_BASE WHERE code = :code"

@Component
class AuthCodeDb : DatabaseHelper() {
    fun getAuthCode(code: Int) = getOne(GET_AUTHCODE_QUERY, AuthCodeDao::class.java, Pair("code", code))
}