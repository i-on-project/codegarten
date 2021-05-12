package org.ionproject.codegarten.responses

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

interface Response {
    @JsonIgnore
    fun getContentType(): String
}

fun Response.toResponseEntity(status: HttpStatus) = ResponseEntity
    .status(status)
    .contentType(MediaType.parseMediaType(this.getContentType()))
    .body(this)