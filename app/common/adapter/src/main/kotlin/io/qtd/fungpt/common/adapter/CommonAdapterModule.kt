package io.qtd.fungpt.common.adapter

import com.fasterxml.jackson.databind.SerializationFeature
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.qtd.fungpt.common.adapter.bases.AdapterModuleCreation
import io.qtd.fungpt.common.adapter.configs.configSwagger
import io.qtd.fungpt.common.adapter.configs.cors
import io.qtd.fungpt.common.adapter.configs.statusPages
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider
import io.qtd.fungpt.common.adapter.databases.PostgresProvider
import io.qtd.fungpt.common.adapter.databases.config.PersistConfig
import io.qtd.fungpt.common.adapter.databases.config.PersistType
import io.qtd.fungpt.common.adapter.databases.config.loadPersistConfig
import io.qtd.fungpt.common.adapter.events.KafkaEventPublisher
import io.qtd.fungpt.common.adapter.events.KafkaEventSubscriber
import io.qtd.fungpt.common.adapter.events.config.loadKafkaConfig
import io.qtd.fungpt.common.adapter.utils.DataTransformationBenchmarkPlugin
import io.qtd.fungpt.common.core.database.BootPersistStoragePort
import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.common.core.database.ShutdownPersistStoragePort
import io.qtd.fungpt.common.core.event.EventPublisherPort
import io.qtd.fungpt.common.core.event.EventSubscriberPort
import org.koin.dsl.binds
import org.koin.dsl.module
import org.slf4j.event.Level

class CommonAdapterModuleCreation : AdapterModuleCreation() {
    override suspend fun preInitDatabase() {

    }

    override fun setupRoutingAndPlugin(app: Application) {
        with(app) {
            install(DefaultHeaders)
            install(CORS) {
                cors()
            }
            install(CallLogging) {
                level = Level.INFO
                callIdMdc("requestId")
            }


            install(CallId) {
                header(HttpHeaders.XRequestId)
                verify { callId: String ->
                    callId.isNotEmpty()
                }
                generate(10)
            }

            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                    enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    findAndRegisterModules()
                }
//                gson {
//                    setPrettyPrinting()
//                    setLenient()
//                    disableHtmlEscaping()
//                    enableComplexMapKeySerialization()
//                    serializeNulls()
//                    setVersion(1.0)
//                }
            }

            install(StatusPages) {
                statusPages()
            }

            install(Resources)

            install(SwaggerUI) {
                configSwagger()
            }

            install(Compression) {
                gzip {
                    minimumSize(minSize = 512L)
                }
            }

            install(DataTransformationBenchmarkPlugin)
        }
    }

    override fun setupKoinModule() = module {
        single { loadPersistConfig(hoconConfig = get()) }
        single { loadKafkaConfig(hoconConfig = get()) }

        single {
            when (get<PersistConfig>().persistType) {
                PersistType.POSTGRES -> PostgresProvider(persistConfig = get())
                PersistType.ES -> ElasticsearchProvider(persistConfig = get())
            }
        } binds arrayOf(
            BootPersistStoragePort::class,
            ShutdownPersistStoragePort::class,
            PersistTransactionPort::class
        )

        single<EventPublisherPort> { KafkaEventPublisher(get()) }
        single<EventSubscriberPort> { KafkaEventSubscriber(get()) }
    }
}

