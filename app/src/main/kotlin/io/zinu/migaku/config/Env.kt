package io.zinu.migaku.config

import io.zinu.migaku.modules.auth.config.JwtConfig
import io.zinu.migaku.modules.auth.config.JwtConfigBuilder
import io.zinu.migaku.modules.database.config.DatabaseConfigBuilder
import io.zinu.migaku.modules.database.config.DatabaseConfig
import io.zinu.migaku.modules.database.config.ESConfig
import io.zinu.migaku.modules.database.config.ESConfigBuilder

data class ApplicationConfig(
    val serverConfig: ServerConfig,
    val jwtConfig: JwtConfig,
    val databaseConfig: DatabaseConfig,
    val esConfig: ESConfig,
)

class ApplicationConfigBuilder {
    private lateinit var serverConfig: ServerConfig
    private lateinit var jwtConfig: JwtConfig
    private lateinit var databaseConfig: DatabaseConfig
    private lateinit var esConfig: ESConfig

    fun server(block: ServerConfigBuilder.() -> Unit) {
        serverConfig = ServerConfigBuilder().apply(block).build()
    }

    fun jwt(block: JwtConfigBuilder.() -> Unit) {
        jwtConfig = JwtConfigBuilder().apply(block).build()
    }

    fun es(block: ESConfigBuilder.() -> Unit) {
        esConfig = ESConfigBuilder().apply(block).build()
    }

    fun database(block: DatabaseConfigBuilder.() -> Unit) {
        databaseConfig = DatabaseConfigBuilder().apply(block).build()
    }


    fun build(): ApplicationConfig = ApplicationConfig(serverConfig, jwtConfig, databaseConfig, esConfig)
}

data class ServerConfig(
    val port: Int
)

class ServerConfigBuilder {
    var port: Int = 0
    fun build(): ServerConfig = ServerConfig(port)
}


fun config(block: ApplicationConfigBuilder.() -> Unit): ApplicationConfig =
    ApplicationConfigBuilder().apply(block).build()

