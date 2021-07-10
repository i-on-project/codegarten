package org.ionproject.codegarten.auth

import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class AuthHeaderValidatorTest {

    private val logger = LoggerFactory.getLogger(AuthHeaderValidatorTest::class.java)

    @Test
    fun testValidAuthHeader() {
        logger.info("Testing valid auth header")
        val user = AuthHeaderValidator.validate("Bearer 123") {
            if (it == "123")
                User(1, "John", 1, "token")
            else
                fail("Validator returned a different token")
        }
        assertEquals(1, user.uid)
    }

    @Test
    fun testInvalidAuthScheme() {
        logger.info("Testing invalid auth scheme")
        assertThrows(AuthorizationException::class.java) {
            AuthHeaderValidator.validate("Basic 123:456") {
                fail("Execution should not reach this point")
            }
            fail("Execution should not reach this point")
        }
    }

    @Test
    fun testNoAuthHeader() {
        logger.info("Testing no auth header")
        assertThrows(AuthorizationException::class.java) {
            AuthHeaderValidator.validate(null) {
                fail("Execution should not reach this point")
            }
            fail("Execution should not reach this point")
        }
    }
}