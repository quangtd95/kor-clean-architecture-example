package io.zinu.migaku.infra

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.zinu.migaku.auth.adapter.authAdapterKoinModule
import io.zinu.migaku.auth.adapter.persist.postgres.entity.RefreshTokens
import io.zinu.migaku.auth.adapter.persist.postgres.entity.Users
import io.zinu.migaku.auth.core.authCoreKoinModule
import io.zinu.migaku.common.adapter.commonAdapterKoinModule
import io.zinu.migaku.common.adapter.config.PersistConfig
import io.zinu.migaku.common.adapter.config.PersistType
import io.zinu.migaku.common.adapter.database.ElasticsearchProvider
import io.zinu.migaku.common.core.commonCoreKoinModule
import io.zinu.migaku.common.core.database.BootPersistStoragePort
import io.zinu.migaku.common.core.database.PersistTransactionPort
import io.zinu.migaku.common.core.database.ShutdownPersistStoragePort
import io.zinu.migaku.infra.config.loadServerConfig
import io.zinu.migaku.user.adapter.userAdapterKoinModule
import io.zinu.migaku.user.core.userCoreKoinModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
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
                    SchemaUtils.create(Users, RefreshTokens)
                }

                PersistType.ES -> {
                    val esProvider = (inject<PersistTransactionPort>().value as ElasticsearchProvider)
                    esProvider.createIndexIfNotExists("users", "refresh_tokens")
                }
            }
        }
    }
}
