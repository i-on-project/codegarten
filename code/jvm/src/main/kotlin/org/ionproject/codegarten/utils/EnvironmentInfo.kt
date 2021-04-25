package org.ionproject.codegarten.utils

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.net.InetAddress

@Component
class EnvironmentInfo {
    @Autowired
    lateinit var environment: Environment

    val port: String by lazy {
        environment.getProperty("local.server.port")!!
    }

    val hostname: String by lazy {
        InetAddress.getLoopbackAddress().hostName
    }

    fun getBaseUrl() = "http://${hostname}:${port}"
}