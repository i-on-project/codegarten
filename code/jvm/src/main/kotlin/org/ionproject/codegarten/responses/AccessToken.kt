package org.ionproject.codegarten.responses

import org.springframework.http.MediaType

class AccessToken(
    val access_token: String,
    val expires_in: Long,
) : Response {

    override fun getContentType() = MediaType.APPLICATION_JSON_VALUE
}