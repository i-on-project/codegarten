package org.ionproject.codegarten.utils

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemReader
import org.springframework.util.Base64Utils
import java.io.File
import java.io.FileReader
import java.security.Key
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.spec.PKCS8EncodedKeySpec
import java.time.OffsetDateTime
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

private const val AUTH_CODE_LENGTH = 20
private const val ACCESS_TOKEN_LENGTH = 32

private const val CIPHER_ALGORITHM = "AES"
private const val CIPHER_TRANSFORMATION = "$CIPHER_ALGORITHM/CBC/PKCS5PADDING"
private const val CIPHER_IV_LENGTH = 16
private val KEY_LENGTH_RANGES = listOf(0..16, 17..24, 25..32)

data class CodeWrapper(
    val code: String,
    val expirationDate: OffsetDateTime
)

class CryptoUtils(cipherKeyPath: String) {

    private val validChars: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private val digester = MessageDigest.getInstance("SHA-256")
    private val cipherKey: ByteArray

    init {
        java.security.Security.addProvider(BouncyCastleProvider())

        // Read cipher key file content into variable (adding padding if necessary)
        val key = File(cipherKeyPath).readText()
        var toAssign: ByteArray? = null
        for (range in KEY_LENGTH_RANGES) {
            if (range.contains(key.length)) {
                toAssign = String.format("%1$-" + range.last + "s", key).toByteArray()
                break
            }
        }

        if (toAssign == null) {
            toAssign = key.substring(0, KEY_LENGTH_RANGES.last().last).toByteArray()
        }

        cipherKey = toAssign
    }

    fun generateAuthCode() = generateRandomCode(AUTH_CODE_LENGTH, OffsetDateTime.now().plusMinutes(1))
    fun generateAccessToken() = generateRandomCode(ACCESS_TOKEN_LENGTH, OffsetDateTime.now().plusWeeks(2))

    fun validateClientSecret(toValidate: String, secretHash: String) = secretHash == hash(toValidate)

    fun hash(toHash: String) = digester.digest(toHash.toByteArray())
        .fold("") { str, byte -> "$str${"%02x".format(byte)}" }

    private fun generateRandomCode(
        length: Int,
        exp: OffsetDateTime
    ) =
        CodeWrapper(
            code = (1..length)
                .map { Random.nextInt(0, validChars.size) }
                .map(validChars::get)
                .joinToString(""),
            expirationDate = exp
        )

    fun encrypt(toEncrypt: String): String {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(
                cipherKey,
                CIPHER_ALGORITHM
            )
        )
        val encryptedValue = cipher.iv + cipher.doFinal(toEncrypt.toByteArray())
        return Base64Utils.encodeToString(encryptedValue)
    }

    fun decrypt(toDecrypt: String): String {
        val decoded = Base64Utils.decode(toDecrypt.toByteArray())
        val iv = decoded.take(CIPHER_IV_LENGTH).toByteArray()
        val content = decoded.drop(CIPHER_IV_LENGTH).toByteArray()

        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(
                cipherKey,
                CIPHER_ALGORITHM
            ),
            IvParameterSpec(iv)
        )
        val decryptedValue = cipher.doFinal(content)
        return String(decryptedValue)
    }

    fun readRsaPrivateKey(filePath: String): Key {
        val keyContent: ByteArray
        FileReader(filePath).use {
            keyContent = PemReader(it).readPemObject().content
        }
        val factory = KeyFactory.getInstance("RSA")
        val keySpec = PKCS8EncodedKeySpec(keyContent)
        return factory.generatePrivate(keySpec)
    }
}