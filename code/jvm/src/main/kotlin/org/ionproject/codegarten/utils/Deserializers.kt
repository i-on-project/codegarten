package org.ionproject.codegarten.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import org.springframework.http.MediaType


class MediaTypeDeserializer : JsonDeserializer<MediaType>() {
    override fun deserialize(jsonParser: JsonParser, context: DeserializationContext) =
        MediaType.valueOf(
            jsonParser.codec.readValue(jsonParser, String::class.java)
        )
}