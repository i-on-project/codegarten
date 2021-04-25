package org.ionproject.codegarten.responses

import com.fasterxml.jackson.annotation.JsonIgnore

interface Response {
    @JsonIgnore
    fun getContentType(): String
}