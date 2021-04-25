package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dto.Client
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

private const val GET_CLIENTS_BASE = "SELECT cid, name, secret, redirect_uri FROM CLIENT"
private const val GET_CLIENT_QUERY = "$GET_CLIENTS_BASE WHERE cid = :cid"

@Component
class ClientsDb(val jdbi: Jdbi) {

    fun getClientById(clientId: Int) =
        jdbi.getOne(GET_CLIENT_QUERY, Client::class.java, mapOf("cid" to clientId))
}