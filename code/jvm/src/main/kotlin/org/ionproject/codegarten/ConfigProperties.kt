package org.ionproject.codegarten

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("cg-server")
data class ConfigProperties(
    val dbUrlConnectionStringEnv: String,
    val dbJdbcConnectionStringEnv: String,

    // GitHub App Info
    val gitHubAppPropertiesEnv: String,
    val gitHubAppPrivateKeyEnv: String,

    // Used to encrypt/decrypt access tokens and other sensitive information
    val cipherKeyEnv: String
)

data class GitHubAppProperties(
    val name: String,
    val id: Int,
    val clientId: String,
    val clientSecret: String
)