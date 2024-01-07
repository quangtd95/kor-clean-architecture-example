package io.qtd.fungpt.infra

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.qtd.fungpt.auth.adapter.AuthAdapterModuleCreation
import io.qtd.fungpt.auth.core.AuthCoreModuleCreation
import io.qtd.fungpt.common.adapter.CommonAdapterModuleCreation
import io.qtd.fungpt.common.core.CommonCoreModuleCreation
import io.qtd.fungpt.common.core.database.BootPersistStoragePort
import io.qtd.fungpt.common.core.database.ShutdownPersistStoragePort
import io.qtd.fungpt.conversation.adapter.ConversationAdapterCreation
import io.qtd.fungpt.conversation.core.ConversationCoreModuleCreation
import io.qtd.fungpt.infra.configs.loadServerConfig
import io.qtd.fungpt.profile.adapter.ProfileAdapterModuleCreation
import io.qtd.fungpt.profile.core.ProfileCoreModuleCreation
import io.qtd.fungpt.profile.core.events.NewProfileSubscriber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Application")

val adapterEntries = listOf(
    CommonAdapterModuleCreation(),
    AuthAdapterModuleCreation(),
    ProfileAdapterModuleCreation(),
    ConversationAdapterCreation()
)

val coreEntries = listOf(
    CommonCoreModuleCreation(),
    AuthCoreModuleCreation(),
    ProfileCoreModuleCreation(),
    ConversationCoreModuleCreation(),
)

fun main(args: Array<String>) {
    val hocon = HoconApplicationConfig(ConfigFactory.load())
    val serverConfig = loadServerConfig(hocon)

    embeddedServer(Netty, port = serverConfig.port) {
        logger.info("Starting instance in port:${serverConfig.port}")

        installKoinModules { hocon }
        bootstrapPersistStorage()
        eventSubscriberModule()
        module()


    }.start(wait = true)
}

fun Application.module() {
    adapterEntries.forEach {
        it.setupRoutingAndPlugin(this)
    }
}

fun Application.eventSubscriberModule() {
    val newProfileSubscriber by inject<NewProfileSubscriber>()
    val job = launch(Dispatchers.IO) {
        newProfileSubscriber.subscribeUserEvents()
    }
    environment.monitor.subscribe(ApplicationStopped) {
        logger.info("newProfileSubscriber is being shutdown...")
        job.cancel()
    }
}

private fun Application.installKoinModules(
    getHocon: () -> HoconApplicationConfig,
) {
    module {
        install(Koin) {
            slf4jLogger(level = Level.INFO)
            modules(
                module { single { getHocon() } },
                *(coreEntries.map { it.setupKoinModule() }).toTypedArray(),
                *(adapterEntries.map { it.setupKoinModule() }).toTypedArray(),
            )
        }
    }
}

private fun Application.bootstrapPersistStorage() {
    val shutdownStoragePort = inject<ShutdownPersistStoragePort>().value
    environment.monitor.subscribe(ApplicationStopped) {
        logger.info("ktor server is being shutdown...")
        shutdownStoragePort.shutdownStorage()
    }

    val bootPersistStoragePort by inject<BootPersistStoragePort>()
    runBlocking(Dispatchers.IO) {
        bootPersistStoragePort.bootStorage {

            adapterEntries.forEach {
                it.preInitDatabase()
            }
        }
    }
}
