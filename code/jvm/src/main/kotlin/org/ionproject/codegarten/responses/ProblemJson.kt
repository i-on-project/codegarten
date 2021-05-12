package org.ionproject.codegarten.responses

import org.springframework.http.MediaType

class ProblemJson(
    val type: String,
    val title: String,
    val status: Int,
    val detail: String,
    val instance: String
) : Response {

    override fun getContentType() = MediaType.APPLICATION_PROBLEM_JSON_VALUE
}