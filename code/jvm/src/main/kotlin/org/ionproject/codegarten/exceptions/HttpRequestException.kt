package org.ionproject.codegarten.exceptions

class HttpRequestException(status: Int) : Exception("HTTP request failed with status $status")