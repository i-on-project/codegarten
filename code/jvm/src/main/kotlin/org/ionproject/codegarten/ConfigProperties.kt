package org.ionproject.codegarten

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("cg-server")
data class ConfigProperties(
    val dbConnectionString: String,

    // GitHub App Info
    val githubAppName: String,
    val githubAppId: String,
    val githubAppClientId: String,
    val githubAppClientSecret: String,
    val githubAppPrivateKey: String,

    // Used to encrypt/decrypt access tokens and other sensitive information
    val cipherKey: String,
)