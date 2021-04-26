package org.ionproject.codegarten.auth

import java.time.OffsetDateTime

data class CodeWrapper(
    val code: String,
    val expirationDate: OffsetDateTime
)

object OAuthUtils {

    private val validChars: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private const val STRING_LENGTH = 20

    fun generateAuthCode() =
        CodeWrapper(
            code = (1..STRING_LENGTH)
                .map { kotlin.random.Random.nextInt(0, validChars.size) }
                .map(validChars::get)
                .joinToString(""),
            expirationDate = OffsetDateTime.now().plusMinutes(1)
        )
}