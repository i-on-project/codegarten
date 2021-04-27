package org.ionproject.codegarten.auth

import org.springframework.util.Base64Utils
import org.springframework.util.Base64Utils.encodeToString
import java.security.MessageDigest
import java.time.OffsetDateTime
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val AUTH_CODE_LENGTH = 20
private const val ACCESS_TOKEN_LENGTH = 32

private const val CIPHER_ALGORITHM = "AES"
private const val CIPHER_TRANSFORMATION = "$CIPHER_ALGORITHM/CBC/PKCS5PADDING"

data class CodeWrapper(
    val code: String,
    val expirationDate: OffsetDateTime
)

class AuthUtils(
    val cipherKey: ByteArray,
    val cipherIv: ByteArray
) {

    private val validChars: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private val digester = MessageDigest.getInstance("SHA-256")

    fun generateAuthCode() = generateRandomCode(AUTH_CODE_LENGTH, OffsetDateTime.now().plusMinutes(1))
    fun generateAccessToken() = generateRandomCode(ACCESS_TOKEN_LENGTH, OffsetDateTime.now().plusWeeks(2))

    fun validateClientSecret(toValidate: String, secretHash: String) = secretHash == hash(toValidate)

    fun hash(toHash: String) = digester.digest(toHash.toByteArray())
        .fold("") { str, byte -> "$str${"%02x".format(byte)}" }

    fun encrypt(toEncrypt: String): String {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(
                cipherKey,
                CIPHER_ALGORITHM
            ),
            IvParameterSpec(cipherIv)
        )

        val encryptedValue = cipher.doFinal(toEncrypt.toByteArray())
        return encodeToString(encryptedValue)
    }

    fun decrypt(toDecrypt: ByteArray): String {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(
                cipherKey,
                CIPHER_ALGORITHM
            ),
            IvParameterSpec(cipherIv)
        )

        val decryptedValue = cipher.doFinal(Base64Utils.decode(toDecrypt))
        return String(decryptedValue)
    }

    private fun generateRandomCode(
        length: Int,
        exp: OffsetDateTime
    ) =
        CodeWrapper(
            code = (1..length)
                .map { kotlin.random.Random.nextInt(0, validChars.size) }
                .map(validChars::get)
                .joinToString(""),
            expirationDate = exp
        )
}