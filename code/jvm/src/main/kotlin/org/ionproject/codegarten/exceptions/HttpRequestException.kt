package org.ionproject.codegarten.exceptions

class HttpRequestException(val status: Int) : Exception("HTTP request failed with status $status")