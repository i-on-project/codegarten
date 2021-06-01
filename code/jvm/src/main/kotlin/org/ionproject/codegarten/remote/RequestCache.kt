package org.ionproject.codegarten.remote

import okhttp3.Call
import okhttp3.Request
import okio.Buffer
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.schedule

data class CachedResponse(
    val body: String?,
    val code: Int,
    val isSuccessful: Boolean
)

class RequestCache {

    private val logger = LoggerFactory.getLogger(RequestCache::class.java)
    private val cache = ConcurrentHashMap<String, CachedResponse>()
    private val timer = Timer()

    fun cache(req: Request, cachedResponse: CachedResponse, expiresIn: Long) {
        val key = req.serialize()

        cache[key] = cachedResponse
        logger.info("Caching response for ${expiresIn}ms: ${req.url}")
        timer.schedule(expiresIn) {
            logger.info("De-caching response: ${req.url}")
            cache.remove(key)
        }
    }

    fun tryGet(req: Request): Optional<CachedResponse> {
        val key = req.serialize()
        val response = cache[key]
        return if (response == null) {
            logger.info("Cache miss: ${req.url}")
            Optional.empty()
        } else {
            logger.info("Cache hit: ${req.url}")
            Optional.of(response)
        }
    }

    private fun Request.serialize(): String {
        val body = this.body
        val toString = this.toString()

        return buildString {
            append(toString)
            if (body != null) {
                val buffer = Buffer()
                body.writeTo(buffer)
                append(buffer.readUtf8())
            }
        }
    }
}

fun Call.executeCached(cache: RequestCache, expiresIn: Long?): CachedResponse {
    val request = this.request()
    if (request.method == "GET" && expiresIn != null) {
        val maybeResponse = cache.tryGet(request)
        if (maybeResponse.isPresent) {
            return maybeResponse.get()
        }
    }

    this.execute().use {
        val cachedResponse = CachedResponse(
            body = it.body?.string(),
            code = it.code,
            isSuccessful = it.isSuccessful
        )
        if (it.isSuccessful && expiresIn != null) {
            cache.cache(request, cachedResponse, expiresIn)
        }

        return cachedResponse
    }
}