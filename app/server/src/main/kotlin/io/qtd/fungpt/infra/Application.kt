package io.qtd.fungpt.infra

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.qtd.fungpt.auth.adapter.authAdapterKoinModule
import io.qtd.fungpt.auth.adapter.preInitEsRepoAuthModule
import io.qtd.fungpt.auth.adapter.preInitPostgresRepoAuthModule
import io.qtd.fungpt.auth.core.authCoreKoinModule
import io.qtd.fungpt.common.adapter.commonAdapterKoinModule
import io.qtd.fungpt.common.adapter.config.PersistConfig
import io.qtd.fungpt.common.adapter.config.PersistType
import io.qtd.fungpt.common.core.commonCoreKoinModule
import io.qtd.fungpt.common.core.database.BootPersistStoragePort
import io.qtd.fungpt.common.core.database.ShutdownPersistStoragePort
import io.qtd.fungpt.infra.config.loadServerConfig
import io.qtd.fungpt.user.adapter.userAdapterKoinModule
import io.qtd.fungpt.user.core.userCoreKoinModule
import kotlinx.coroutines.Dispatchers
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

        installKoinModules { hocon }
        bootstrapPersistStorage()
        module()

    }.start(wait = true)
}

private fun Application.installKoinModules(getHocon: () -> HoconApplicationConfig) {
    module {
        install(Koin) {
            slf4jLogger(level = Level.INFO)
            modules(
                module {
                    single { getHocon() }
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
}

private fun Application.bootstrapPersistStorage() {
    val logger = LoggerFactory.getLogger("Application")
    val shutdownStoragePort = inject<ShutdownPersistStoragePort>().value
    environment.monitor.subscribe(ApplicationStopped) {
        logger.info("ktor server is being shutdown...")
        shutdownStoragePort.shutdownStorage()
    }

    val bootPersistStoragePort by inject<BootPersistStoragePort>()
    runBlocking(Dispatchers.IO) {
        bootPersistStoragePort.bootStorage {
            val persistType = inject<PersistConfig>().value.persistType
            when (persistType) {
                PersistType.POSTGRES -> {
                    preInitPostgresRepoAuthModule()
                }

                PersistType.ES -> {
                    preInitEsRepoAuthModule()
                }
            }
        }
    }
}
