package io.zinu.migaku

import io.zinu.migaku.config.*
import io.zinu.migaku.modules.auth.config.authKoinModule
import io.zinu.migaku.modules.database.config.databaseKoinModule
import io.zinu.migaku.modules.database.config.esKoinModule
import io.zinu.migaku.modules.profile.config.profileKoinModule
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    val config = extractConfig(HoconApplicationConfig(ConfigFactory.load()))

    embeddedServer(Netty, port = config.serverConfig.port) {
        println("Starting instance in port:${config.serverConfig.port}")

        module {
            install(Koin) {
                modules(
                    module { single { config } },
                    databaseKoinModule,
                    esKoinModule,
                    authKoinModule,
                    profileKoinModule,
                )
            }
        }
        main()

    }.start(wait = true)
}

fun Application.main() {
    module()
}
