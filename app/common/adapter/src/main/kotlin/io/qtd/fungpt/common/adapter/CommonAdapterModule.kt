package io.qtd.fungpt.common.adapter

import com.fasterxml.jackson.databind.SerializationFeature
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.qtd.fungpt.common.adapter.config.*
import io.qtd.fungpt.common.adapter.database.ElasticsearchProvider
import io.qtd.fungpt.common.adapter.database.PostgresProvider
import io.qtd.fungpt.common.adapter.database.config.PersistConfig
import io.qtd.fungpt.common.adapter.database.config.PersistType
import io.qtd.fungpt.common.adapter.database.config.loadPersistConfig
import io.qtd.fungpt.common.adapter.event.KafkaEventPublisher
import io.qtd.fungpt.common.adapter.event.KafkaEventSubscriber
import io.qtd.fungpt.common.adapter.event.config.loadKafkaConfig
import io.qtd.fungpt.common.adapter.utils.DataTransformationBenchmarkPlugin
import io.qtd.fungpt.common.core.database.BootPersistStoragePort
import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.common.core.database.ShutdownPersistStoragePort
import io.qtd.fungpt.common.core.event.EventPublisherPort
import io.qtd.fungpt.common.core.event.EventSubscriberPort
import org.koin.dsl.binds
import org.koin.dsl.module
import org.slf4j.event.Level

val commonAdapterKoinModule = module {
    single { loadPersistConfig(hoconConfig = get()) }
    single { loadKafkaConfig(hoconConfig = get()) }

    single {
        when (get<PersistConfig>().persistType) {
            PersistType.POSTGRES -> PostgresProvider(persistConfig = get())
            PersistType.ES -> ElasticsearchProvider(persistConfig = get())
        }
    } binds arrayOf(
        BootPersistStoragePort::class, ShutdownPersistStoragePort::class, PersistTransactionPort::class
    )

    single<EventPublisherPort> { KafkaEventPublisher(get()) }
    single<EventSubscriberPort> { KafkaEventSubscriber(get()) }
}


fun Application.commonModule() {
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
    }

    install(StatusPages) {
        statusPages()
    }

    install(Resources)

    install(SwaggerUI) {
        configSwagger()
    }

    install(DataTransformationBenchmarkPlugin)
}