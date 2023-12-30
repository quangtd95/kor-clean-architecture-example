package io.zinu.migaku.infra

import com.auth0.jwt.interfaces.JWTVerifier
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
import io.zinu.migaku.auth.adapter.persist.postgres.entity.RefreshTokens
import io.zinu.migaku.auth.adapter.persist.postgres.entity.Users
import io.zinu.migaku.common.config.CommonConfig
import io.zinu.migaku.common.config.configSwagger
import io.zinu.migaku.common.config.cors
import io.zinu.migaku.common.config.statusPages
import io.zinu.migaku.common.database.IDatabaseProvider
import io.zinu.migaku.common.database.IESProvider
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject
import org.slf4j.event.Level


fun Application.module() {
    val config: CommonConfig by inject()
    val tokenJWTVerifier: JWTVerifier by inject()
    val databaseProvider: IDatabaseProvider by inject()
    val esProvider: IESProvider by inject()

    databaseProvider.init {
        transaction {
            create(Users, RefreshTokens)
        }
    }
    esProvider.init()

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

    install(SwaggerUI) {
        configSwagger()
    }

}


