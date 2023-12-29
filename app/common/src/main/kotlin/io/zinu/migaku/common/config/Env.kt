package io.zinu.migaku.common.config

import io.ktor.server.config.*
import io.zinu.migaku.common.database.config.DatabaseConfigBuilder
import io.zinu.migaku.common.database.config.DatabaseConfig
import io.zinu.migaku.common.database.config.ESConfig
import io.zinu.migaku.common.database.config.ESConfigBuilder

data class CommonConfig(
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


    fun build(): CommonConfig = CommonConfig(serverConfig, jwtConfig, databaseConfig, esConfig)
}

data class ServerConfig(
    val port: Int
)

class ServerConfigBuilder {
    var port: Int = 0
    fun build(): ServerConfig = ServerConfig(port)
}


fun config(block: ApplicationConfigBuilder.() -> Unit): CommonConfig =
    ApplicationConfigBuilder().apply(block).build()


fun extractConfig(hoconConfig: HoconApplicationConfig) = config {
    server {
        port = hoconConfig.config("ktor.deployment").property("port").getString().toInt()
    }
    jwt {
        with(hoconConfig.config("jwt")) {
            accessTokenSecretKey = property("accessTokenSecretKey").getString()
            refreshTokenSecretKey = property("refreshTokenSecretKey").getString()
            realm = property("realm").getString()
            issuer = property("issuer").getString()
            audience = property("audience").getString()
        }
    }
    database {
        with(hoconConfig.config("database")) {
            driverClassName = property("driverClassName").getString()
            jdbcUrl = property("jdbcUrl").getString()
            maximumPoolSize = property("maximumPoolSize").getString().toInt()
            isAutoCommit = property("isAutoCommit").getString().toBoolean()
            transactionIsolation = property("transactionIsolation").getString()
        }
    }

    es {
        with(hoconConfig.config("es")) {
            host = property("host").getString()
            port = property("port").getString().toInt()
            user = property("user").getString()
            password = property("password").getString()
            https = property("https").getString().toBoolean()
        }
    }
}