package io.qtd.fungpt.infra.configs


data class ServerConfig(
    val port: Int
)

class ServerConfigBuilder {
    var port: Int = 0
    fun build(): ServerConfig = ServerConfig(port)
}

fun server(block: ServerConfigBuilder.() -> Unit): ServerConfig =
    ServerConfigBuilder().apply(block).build()
