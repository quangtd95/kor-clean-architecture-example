package io.zinu.migaku.infra

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.zinu.migaku.auth.adapter.authAdapterKoinModule
import io.zinu.migaku.auth.adapter.authModule
import io.zinu.migaku.auth.core.authCoreKoinModule
import io.zinu.migaku.auth.core.repository.BootPersistStoragePort
import io.zinu.migaku.auth.core.repository.ShutdownPersistStoragePort
import io.zinu.migaku.common.config.loadCommonConfig
import io.zinu.migaku.common.database.config.databaseKoinModule
import io.zinu.migaku.common.database.config.esKoinModule
import io.zinu.migaku.user.adapter.userAdapterKoinModule
import io.zinu.migaku.user.adapter.userModule
import io.zinu.migaku.user.core.userCoreKoinModule
import kotlinx.coroutines.runBlocking
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val hocon = HoconApplicationConfig(ConfigFactory.load())
    val config = loadCommonConfig(hocon)
    val logger = LoggerFactory.getLogger("Application")

    embeddedServer(Netty, port = config.serverConfig.port) {
        logger.info("Starting instance in port:${config.serverConfig.port}")

        module {
            install(Koin) {
                slf4jLogger(level = Level.INFO)
                modules(
                    module {
                        single { hocon }
                        single { config }
                    },
                    databaseKoinModule,
                    esKoinModule,
                    authCoreKoinModule,
                    authAdapterKoinModule,

                    userCoreKoinModule,
                    userAdapterKoinModule,
                )
            }
        }

        val shutdownStoragePort = inject<ShutdownPersistStoragePort>().value
        environment.monitor.subscribe(ApplicationStopped) {
            logger.info("ktor server is being shutdown...")
            shutdownStoragePort.shutdownStorage()
        }

        val bootPersistStoragePort by inject<BootPersistStoragePort>()
        runBlocking {
            bootPersistStoragePort.bootStorage {}
        }

        main()

    }.start(wait = true)
}

fun Application.main() {
    module()
    authModule()
    userModule()
}
