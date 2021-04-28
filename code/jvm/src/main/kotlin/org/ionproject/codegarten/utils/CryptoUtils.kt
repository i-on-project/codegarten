package org.ionproject.codegarten.utils

import org.bouncycastle.util.io.pem.PemReader
import org.springframework.util.Base64Utils
import org.springframework.util.Base64Utils.encodeToString
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

data class CodeWrapper(
    val code: String,
    val expirationDate: OffsetDateTime
)

class CryptoUtils(
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

    fun readRsaPrivateKey(filePath: String): Key {
        val keyContent: ByteArray
        FileReader(filePath).use {
            keyContent = PemReader(it).readPemObject().content
        }
        val factory = KeyFactory.getInstance("RSA")
        val keySpec = PKCS8EncodedKeySpec(keyContent)
        return factory.generatePrivate(keySpec)

        /*
        val key = File(filePath)
            .readText()
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replace("\n", "")
            .toByteArray()

        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = PKCS8EncodedKeySpec(readPkcs1PrivateKey(Base64Utils.decode(key)))
        return keyFactory.generatePrivate(keySpec) */
    }

    private fun readPkcs1PrivateKey(pkcs1Bytes: ByteArray): ByteArray {
        // We can't use Java internal APIs to parse ASN.1 structures, so we build a PKCS#8 key Java can understand
        val pkcs1Length = pkcs1Bytes.size
        val totalLength = pkcs1Length + 22
        val pkcs8Header = byteArrayOf(
            0x30, 0x82.toByte(), (totalLength shr 8 and 0xff).toByte(), (totalLength and 0xff).toByte(), // Sequence + total length
            0x2, 0x1, 0x0,  // Integer (0)
            0x30, 0xD, 0x6, 0x9, 0x2A, 0x86.toByte(), 0x48, 0x86.toByte(), 0xF7.toByte(), 0xD, 0x1, 0x1, 0x1, 0x5, 0x0,  // Sequence: 1.2.840.113549.1.1.1, NULL
            0x4, 0x82.toByte(), (pkcs1Length shr 8 and 0xff).toByte(), (pkcs1Length and 0xff).toByte() // Octet string + length
        )
        return join(pkcs8Header, pkcs1Bytes)
    }

    private fun join(byteArray1: ByteArray, byteArray2: ByteArray): ByteArray {
        val bytes = ByteArray(byteArray1.size + byteArray2.size)
        System.arraycopy(byteArray1, 0, bytes, 0, byteArray1.size)
        System.arraycopy(byteArray2, 0, bytes, byteArray1.size, byteArray2.size)
        return bytes
    }
}