package io.zinu.migaku.infra

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.zinu.migaku.auth.adapter.authAdapterKoinModule
import io.zinu.migaku.auth.adapter.preInitAuthAdapterModule
import io.zinu.migaku.auth.core.authCoreKoinModule
import io.zinu.migaku.common.adapter.commonAdapterKoinModule
import io.zinu.migaku.common.adapter.database.IESProvider
import io.zinu.migaku.common.core.commonCoreKoinModule
import io.zinu.migaku.common.core.database.BootPersistStoragePort
import io.zinu.migaku.common.core.database.ShutdownPersistStoragePort
import io.zinu.migaku.infra.config.loadServerConfig
import io.zinu.migaku.user.adapter.preInitUserAdapterModule
import io.zinu.migaku.user.adapter.userAdapterKoinModule
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
    val serverConfig = loadServerConfig(hocon)
    val logger = LoggerFactory.getLogger("Application")

    embeddedServer(Netty, port = serverConfig.port) {
        logger.info("Starting instance in port:${serverConfig.port}")

        module {
            install(Koin) {
                slf4jLogger(level = Level.INFO)
                modules(
                    module {
                        single { hocon }
                    },
                    commonCoreKoinModule,
                    commonAdapterKoinModule,

                    authCoreKoinModule,
                    authAdapterKoinModule,

                    userCoreKoinModule,
                    userAdapterKoinModule,
                )
            }
        }

        val esProvider: IESProvider by inject()
        esProvider.init()

        val shutdownStoragePort = inject<ShutdownPersistStoragePort>().value
        environment.monitor.subscribe(ApplicationStopped) {
            logger.info("ktor server is being shutdown...")
            shutdownStoragePort.shutdownStorage()
        }

        val bootPersistStoragePort by inject<BootPersistStoragePort>()
        runBlocking {
            bootPersistStoragePort.bootStorage {
                preInitAuthAdapterModule()
                preInitUserAdapterModule()
            }
        }

        main()

    }.start(wait = true)
}

fun Application.main() {
    module()

}
