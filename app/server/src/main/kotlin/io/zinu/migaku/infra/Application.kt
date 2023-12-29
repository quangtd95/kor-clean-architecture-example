package io.zinu.migaku.infra

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.zinu.migaku.auth.config.authKoinModule
import io.zinu.migaku.common.config.extractConfig
import io.zinu.migaku.common.database.config.databaseKoinModule
import io.zinu.migaku.common.database.config.esKoinModule
import io.zinu.migaku.user.config.userKoinModule
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    val config = extractConfig(HoconApplicationConfig(ConfigFactory.load()))

    embeddedServer(Netty, port = config.serverConfig.port) {
        println("Starting instance in port:${config.serverConfig.port}")

        module {
            install(Koin) {
                slf4jLogger()
                modules(
                    module { single { config } },
                    databaseKoinModule,
                    esKoinModule,
                    authKoinModule,
                    userKoinModule,
                )
            }
        }
        main()

    }.start(wait = true)
}

fun Application.main() {
    module()
}
