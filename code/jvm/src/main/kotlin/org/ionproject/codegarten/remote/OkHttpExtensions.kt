package org.ionproject.codegarten.remote

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.ionproject.codegarten.exceptions.HttpRequestException
import org.ionproject.codegarten.remote.github.GitHubRoutes
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
    val res = this.newCall(request).execute()
    if (res.code in 400 until 600) throw HttpRequestException(res.code)

    try {
        return mapper.readValue(res.body!!.string(), mapTo)
    } catch (ex: JsonMappingException) {
        throw HttpRequestException(res.code)
    }
}

fun <T> OkHttpClient.callAndMapList(request: Request, mapper: ObjectMapper, mapTo: Class<T>): List<T> {
    val res = this.newCall(request).execute()
    if (res.code in 400 until 600) throw HttpRequestException(res.code)

    try {
        return mapper.readValue(
            res.body!!.string(),
            mapper.typeFactory.constructCollectionType(List::class.java, mapTo)
        )
    } catch (ex: JsonMappingException) {
        throw HttpRequestException(res.code)
    }
}