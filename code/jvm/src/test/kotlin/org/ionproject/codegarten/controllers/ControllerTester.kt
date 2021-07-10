package org.ionproject.codegarten.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.ionproject.codegarten.TestConfiguration
import org.ionproject.codegarten.database.DatabaseInitializer
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.net.URI

@Import(TestConfiguration::class)
@ExtendWith(DatabaseInitializer::class)
@SpringBootTest
@AutoConfigureMockMvc
class ControllerTester {
    @Autowired
    lateinit var mocker: MockMvc
    @Autowired
    lateinit var mapper: ObjectMapper

    fun doGet(uri: URI, dsl: MockHttpServletRequestDsl.() -> Unit = {}) = mocker.get(uri, dsl)

    fun doPost(uri: URI, dsl: MockHttpServletRequestDsl.() -> Unit = {}) = mocker.post(uri, dsl)

    fun doPut(uri: URI, dsl: MockHttpServletRequestDsl.() -> Unit = {}) = mocker.put(uri, dsl)

    fun doDelete(uri: URI, dsl: MockHttpServletRequestDsl.() -> Unit = {}) = mocker.delete(uri, dsl)
}