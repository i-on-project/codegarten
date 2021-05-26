package org.ionproject.codegarten

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("cg-server")
data class ConfigProperties(
    val dbConnectionStringEnv: String,

    // GitHub App Info
    val gitHubAppPropertiesPathEnv: String,
    val gitHubAppPrivateKeyPemPath: String,

    // Used to encrypt/decrypt access tokens and other sensitive information
    val cipherKeyEnv: String
)

data class GitHubAppProperties(
    val name: String,
    val id: Int,
    val clientId: String,
    val clientSecret: String
)