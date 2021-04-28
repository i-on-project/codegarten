package org.ionproject.codegarten.remote

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI


fun Request.Builder.from(uri: URI, clientName: String, token: String? = null) =
    this.from(uri.toString(), clientName, token)

fun Request.Builder.from(uri: String, clientName: String, token: String? = null): Request.Builder {
    val toReturn = this.url(uri)
        .addHeader("Accept", GitHubRoutes.ACCEPT_CONTENT_TYPE)
        .addHeader("User-Agent", clientName)
    if (token != null) toReturn.addHeader("Authorization", "Bearer $token")
    return toReturn
}

fun <T> OkHttpClient.callAndMap(request: Request, mapper: ObjectMapper, mapTo: Class<T>): T {
    val resBody = this.newCall(request).execute().body!!.string()
    return mapper.readValue(resBody, mapTo)
}