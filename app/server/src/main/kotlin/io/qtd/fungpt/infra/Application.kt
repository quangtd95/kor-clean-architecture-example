package io.qtd.fungpt.infra

import com.fasterxml.jackson.databind.json.JsonMapper
import com.typesafe.config.ConfigFactory
import io.github.nomisRev.kafka.Admin
import io.github.nomisRev.kafka.AdminSettings
import io.github.nomisRev.kafka.createTopic
import io.github.nomisRev.kafka.map
import io.github.nomisRev.kafka.receiver.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.qtd.fungpt.auth.adapter.authAdapterKoinModule
import io.qtd.fungpt.auth.adapter.preInitEsRepoAuthModule
import io.qtd.fungpt.auth.adapter.preInitPostgresRepoAuthModule
import io.qtd.fungpt.auth.core.authCoreKoinModule
import io.qtd.fungpt.auth.core.model.CoreUser
import io.qtd.fungpt.common.adapter.commonAdapterKoinModule
import io.qtd.fungpt.common.adapter.config.PersistConfig
import io.qtd.fungpt.common.adapter.config.PersistType
import io.qtd.fungpt.common.core.commonCoreKoinModule
import io.qtd.fungpt.common.core.database.BootPersistStoragePort
import io.qtd.fungpt.common.core.database.ShutdownPersistStoragePort
import io.qtd.fungpt.common.core.event.Event
import io.qtd.fungpt.infra.config.loadServerConfig
import io.qtd.fungpt.profile.adapter.preInitEsRepoProfileModule
import io.qtd.fungpt.profile.adapter.profileAdapterKoinModule
import io.qtd.fungpt.profile.core.profileCoreKoinModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.serialization.StringDeserializer
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) {
    val hocon = HoconApplicationConfig(ConfigFactory.load())
    val serverConfig = loadServerConfig(hocon)
    val logger = LoggerFactory.getLogger("Application")

    embeddedServer(Netty, port = serverConfig.port) {
        logger.info("Starting instance in port:${serverConfig.port}")

        installKoinModules { hocon }
        bootstrapPersistStorage()
        module()

        val job = launch(Dispatchers.IO) {
            delay(5000)
            val receiverSettings = ReceiverSettings(
                bootstrapServers = "localhost:29092",
                keyDeserializer = StringDeserializer(),
                valueDeserializer = StringDeserializer().map {
                    JsonMapper.builder()
                        .findAndAddModules()
                        .build()
                        .readValue(it, Event::class.java) as Event<CoreUser>
                },
                groupId = "get-user-created-events",
                properties = Properties(),
            )

            KafkaReceiver(receiverSettings).receive("test")
                .cancellable()
                .collect {
                    println("new event: $it")
                }
        }

        environment.monitor.subscribe(ApplicationStopped) {
            logger.info("kafkaReceiver is being shutdown...")
            job.cancel()
        }

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

                profileCoreKoinModule,
                profileAdapterKoinModule,
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
                    //TODO: profile module hasn't supported postgres yet
                }

                PersistType.ES -> {
                    preInitEsRepoAuthModule()
                    preInitEsRepoProfileModule()
                }
            }
        }
    }
}
