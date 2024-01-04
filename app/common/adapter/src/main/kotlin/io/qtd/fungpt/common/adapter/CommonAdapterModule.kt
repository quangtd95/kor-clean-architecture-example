package io.qtd.fungpt.common.adapter

import com.fasterxml.jackson.databind.SerializationFeature
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.qtd.fungpt.common.adapter.config.*
import io.qtd.fungpt.common.adapter.database.*
import io.qtd.fungpt.common.adapter.event.DummyEventPublisher
import io.qtd.fungpt.common.adapter.utils.DataTransformationBenchmarkPlugin
import io.qtd.fungpt.common.core.database.BootPersistStoragePort
import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.common.core.database.ShutdownPersistStoragePort
import io.qtd.fungpt.common.core.event.EventPublisherPort
import org.koin.dsl.binds
import org.koin.dsl.module
import org.slf4j.event.Level

val commonAdapterKoinModule = module {
    single { loadPersistConfig(hoconConfig = get()) }

    single {
        when (get<PersistConfig>().persistType) {
            PersistType.POSTGRES -> PostgresProvider(persistConfig = get())
            PersistType.ES -> ElasticsearchProvider(persistConfig = get())
        }
    } binds arrayOf(
        BootPersistStoragePort::class, ShutdownPersistStoragePort::class, PersistTransactionPort::class
    )

    single<EventPublisherPort> { DummyEventPublisher() }
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