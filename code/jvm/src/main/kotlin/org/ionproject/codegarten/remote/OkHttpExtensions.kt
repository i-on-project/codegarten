package org.ionproject.codegarten.remote

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import org.ionproject.codegarten.exceptions.HttpRequestException
import org.ionproject.codegarten.remote.github.GitHubRoutes
import java.net.URI


val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

val requestCache = RequestCache()

fun Request.Builder.from(uri: URI, clientName: String, token: String? = null) =
    this.from(uri.toString(), clientName, token)

fun Request.Builder.from(uri: String, clientName: String, token: String? = null): Request.Builder {
    val toReturn = this.url(uri)
        .addHeader("Accept", GitHubRoutes.ACCEPT_CONTENT_TYPE)
        .addHeader("User-Agent", clientName)

    if (token != null) toReturn.addHeader("Authorization", "Bearer $token")
    return toReturn
}

fun OkHttpClient.call(request: Request) {
    val res = this.newCall(request).execute()
    if (!res.isSuccessful) throw HttpRequestException(res.code, res.body?.string())
}

fun <T> OkHttpClient.callAndMap(request: Request, mapper: ObjectMapper, mapTo: Class<T>, cacheExpiresIn: Long? = null): T {
    val call = this.newCall(request)
    val res = call.executeCached(requestCache, cacheExpiresIn)
    if (!res.isSuccessful) throw HttpRequestException(res.code, res.body)

    val body = res.body
    try {
        return mapper.readValue(body, mapTo)
    } catch (ex: JsonMappingException) {
        throw HttpRequestException(res.code, body)
    }
}

fun <T> OkHttpClient.callAndMapList(request: Request, mapper: ObjectMapper, mapTo: Class<T>, cacheExpiresIn: Long? = null): List<T> {
    val call = this.newCall(request)
    val res = call.executeCached(requestCache, cacheExpiresIn)
    if (!res.isSuccessful) throw HttpRequestException(res.code, res.body)

    val body = res.body
    try {
        return mapper.readValue(body, mapper.typeFactory.constructCollectionType(List::class.java, mapTo))
    } catch (ex: JsonMappingException) {
        throw HttpRequestException(res.code, body)
    }
}