package org.ionproject.codegarten.responses

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.CollectionUtils

interface Response {
    @JsonIgnore
    fun getContentType(): String
}

fun Response.toResponseEntity(status: HttpStatus) = ResponseEntity
    .status(status)
    .contentType(MediaType.parseMediaType(this.getContentType()))
    .body(this)

fun Response.toResponseEntity(status: HttpStatus, headers: Map<String, List<String>>?) : ResponseEntity<Response> {
    val toReturn = ResponseEntity
        .status(status)
        .contentType(MediaType.parseMediaType(this.getContentType()))
    if (headers != null) {
        toReturn.headers(HttpHeaders(CollectionUtils.toMultiValueMap(headers)))
    }
    return toReturn.body(this)
}

