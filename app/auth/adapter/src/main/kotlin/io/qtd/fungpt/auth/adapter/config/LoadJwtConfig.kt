package io.qtd.fungpt.auth.adapter.config

import io.ktor.server.config.*
import io.qtd.fungpt.auth.core.config.JwtConfig

fun loadJwtConfig(hoconConfig: HoconApplicationConfig) = with(hoconConfig.config("jwt")) {
    JwtConfig(
        accessTokenSecretKey = property("accessTokenSecretKey").getString(),
        refreshTokenSecretKey = property("refreshTokenSecretKey").getString(),
        realm = property("realm").getString(),
        issuer = property("issuer").getString(),
        audience = property("audience").getString(),
    )
}