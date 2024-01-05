package io.qtd.fungpt.infra.configs

import io.ktor.server.config.*

fun loadServerConfig(hoconConfig: HoconApplicationConfig) = server {
    port = hoconConfig.config("ktor.deployment").property("port").getString().toInt()
}