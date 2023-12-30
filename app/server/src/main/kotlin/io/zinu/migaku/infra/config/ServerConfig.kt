package io.zinu.migaku.infra.config


data class ServerConfig(
    val port: Int
)

class ServerConfigBuilder {
    var port: Int = 0
    fun build(): ServerConfig = ServerConfig(port)
}

fun server(block: ServerConfigBuilder.() -> Unit): ServerConfig =
    ServerConfigBuilder().apply(block).build()
