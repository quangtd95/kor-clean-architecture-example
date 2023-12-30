package io.zinu.migaku.auth.adapter

import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.zinu.migaku.auth.adapter.api.rest.auth
import io.zinu.migaku.auth.adapter.config.configureJwtAuthentication
import io.zinu.migaku.auth.adapter.config.loadJwtConfig
import io.zinu.migaku.auth.adapter.password.PasswordChecker
import io.zinu.migaku.auth.adapter.persist.postgres.repository.RefreshTokenRepository
import io.zinu.migaku.auth.adapter.persist.postgres.repository.UserRepository
import io.zinu.migaku.auth.adapter.token.TokenGenerator
import io.zinu.migaku.auth.core.config.JwtConfig
import io.zinu.migaku.auth.core.repository.*
import io.zinu.migaku.auth.core.repository.TokenGeneratorPort
import org.koin.dsl.module
import org.koin.ktor.ext.inject

val authAdapterKoinModule = module {
    single {
        loadJwtConfig(get())
    }
    single<PasswordCheckerPort> { PasswordChecker }
    single<TokenGeneratorPort> { TokenGenerator(get()) }
    single<UserPort> { UserRepository }
    single<JWTVerifier> { (get<TokenGeneratorPort>() as TokenGenerator).getTokenVerifier() }
    single<RefreshTokenPort> { RefreshTokenRepository }

}

fun Application.authModule() {
    val jwtConfig by inject<JwtConfig>()
    val tokenJWTVerifier by inject<JWTVerifier>()

    install(Authentication) {
        configureJwtAuthentication(jwtConfig, tokenJWTVerifier)
    }

    routing {
        route("/api") {
            auth()
        }
    }
}
