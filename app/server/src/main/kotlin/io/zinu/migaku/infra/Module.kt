package io.zinu.migaku.infra

import com.auth0.jwt.interfaces.JWTVerifier
import com.fasterxml.jackson.databind.SerializationFeature
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import io.zinu.migaku.auth.api.auth
import io.zinu.migaku.auth.config.jwtConfig
import io.zinu.migaku.auth.model.RefreshTokens
import io.zinu.migaku.auth.model.Users
import io.zinu.migaku.common.config.CommonConfig
import io.zinu.migaku.common.config.configSwagger
import io.zinu.migaku.common.config.cors
import io.zinu.migaku.common.config.statusPages
import io.zinu.migaku.common.database.IDatabaseProvider
import io.zinu.migaku.common.database.IESProvider
import io.zinu.migaku.user.api.user
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
    }

    install(Authentication) {
        jwtConfig(config.jwtConfig, tokenJWTVerifier)
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

    routing {
        route("/api") {
            auth()
            user()
        }
    }
}


