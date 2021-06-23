package org.ionproject.codegarten.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CryptoUtilsTest {

    @Autowired
    private lateinit var cryptoUtils: CryptoUtils

    @Test
    fun testEncryptionAndDecryption() {
        val message = "myEncryptedMessage"

        val encrypted = cryptoUtils.encrypt(message)
        assertNotEquals(message, encrypted)
        val decrypted = cryptoUtils.decrypt(encrypted)
        assertEquals(message, decrypted)
    }

    @Test
    fun testEncryptionAndDecryptionWithDifferentMessages() {
        val message1 = "myEncryptedMessage"
        val message2 = "myOtherEncryptedMessage"

        val encrypted1 = cryptoUtils.encrypt(message1)
        val encrypted2 = cryptoUtils.encrypt(message2)
        assertNotEquals(message1, encrypted1)
        assertNotEquals(message2, encrypted2)
        assertNotEquals(encrypted1, encrypted2)

        val decrypted1 = cryptoUtils.decrypt(encrypted1)
        val decrypted2 = cryptoUtils.decrypt(encrypted2)
        assertEquals(message1, decrypted1)
        assertEquals(message2, decrypted2)
        assertNotEquals(decrypted1, decrypted2)
    }

    @Test
    fun testValidateHashSuccessfully() {
        val message = "toHash"

        val hash = cryptoUtils.hash(message)
        assertTrue(cryptoUtils.validateHash(message, hash))
    }

    @Test
    fun testValidateHashUnsuccessfully() {
        val message = "toHash"

        val hash = cryptoUtils.hash("otherToHash")
        assertFalse(cryptoUtils.validateHash(message, hash))
    }
}