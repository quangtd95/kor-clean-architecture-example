package io.zinu.migaku.infra.config

import io.ktor.server.config.*

fun loadServerConfig(hoconConfig: HoconApplicationConfig) = server {
    port = hoconConfig.config("ktor.deployment").property("port").getString().toInt()
}