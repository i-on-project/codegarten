package org.ionproject.codegarten

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("cg-server")
data class ConfigProperties(
    val dbConnectionString: String,

    // GitHub App Info
    val gitHubAppName: String,
    val gitHubAppId: Int,
    val gitHubAppClientId: String,
    val gitHubAppClientSecret: String,
    val gitHubAppPrivateKeyPemPath: String,

    // Used to encrypt/decrypt access tokens and other sensitive information
    val cipherKey: String,
    val cipherIv: String, // 16 Bytes
)