package org.ionproject.codegarten.exceptions

class HttpRequestException(val status: Int, val responseBody: String? = null) : Exception("HTTP request failed with status $status")