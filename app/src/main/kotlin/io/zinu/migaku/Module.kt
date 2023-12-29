package io.zinu.migaku

import com.auth0.jwt.interfaces.JWTVerifier
import com.fasterxml.jackson.databind.SerializationFeature
import io.zinu.migaku.config.ApplicationConfig
import io.zinu.migaku.config.configSwagger
import io.zinu.migaku.config.cors
import io.zinu.migaku.config.statusPages
import io.zinu.migaku.modules.api
import io.zinu.migaku.modules.auth.config.jwtConfig
import io.zinu.migaku.modules.database.IDatabaseProvider
import io.zinu.migaku.modules.database.IESProvider
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
import org.koin.ktor.ext.inject
import org.slf4j.event.Level


fun Application.module() {
    val config: ApplicationConfig by inject()
    val tokenJWTVerifier: JWTVerifier by inject()
    val databaseProvider: io.zinu.migaku.modules.database.IDatabaseProvider by inject()
    val esProvider: IESProvider by inject()

    databaseProvider.init()
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
        api()
    }
}


