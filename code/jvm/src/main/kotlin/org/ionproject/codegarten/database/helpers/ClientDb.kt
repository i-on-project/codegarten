package org.ionproject.codegarten.database.helpers

import org.ionproject.codegarten.database.dao.ClientDao
import org.springframework.stereotype.Component

private const val GET_CLIENTS_BASE = "SELECT cid, name, secret, redirect_uri FROM CLIENT"
private const val GET_CLIENT_QUERY = "$GET_CLIENTS_BASE WHERE cid = :cid"

@Component
class ClientDb : DatabaseHelper() {
    fun getClientById(clientId: Int) = getOne(GET_CLIENT_QUERY, ClientDao::class.java, Pair("cid", clientId))
}