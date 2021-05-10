package org.ionproject.codegarten.auth

import org.ionproject.codegarten.database.dto.User
import org.ionproject.codegarten.exceptions.AuthorizationException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AuthHeaderValidatorTest {

    @Test
    fun testValidAuthHeader() {
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
        assertThrows(AuthorizationException::class.java) {
            AuthHeaderValidator.validate("Basic 123:456") {
                fail("Execution should not reach this point")
            }
            fail("Execution should not reach this point")
        }
    }

    @Test
    fun testNoAuthHeader() {
        assertThrows(AuthorizationException::class.java) {
            AuthHeaderValidator.validate(null) {
                fail("Execution should not reach this point")
            }
            fail("Execution should not reach this point")
        }
    }
}