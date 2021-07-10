package org.ionproject.codegarten.utils

import com.fasterxml.jackson.databind.ObjectMapper

fun Any.toJson(mapper: ObjectMapper) = mapper.writeValueAsString(this)