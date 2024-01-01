package io.qtd.fungpt.auth.core.config

data class JwtConfig(
    val accessTokenSecretKey: String,
    val refreshTokenSecretKey: String,
    val issuer: String,
    val audience: String,
    val realm: String
)

class JwtConfigBuilder {
    lateinit var accessTokenSecretKey: String
    lateinit var refreshTokenSecretKey: String
    lateinit var issuer: String
    lateinit var audience: String
    lateinit var realm: String

    fun build(): JwtConfig = JwtConfig(accessTokenSecretKey, refreshTokenSecretKey, issuer, audience, realm)
}