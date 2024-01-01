package io.zinu.migaku.auth.adapter

import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.zinu.migaku.auth.adapter.api.rest.auth
import io.zinu.migaku.auth.adapter.config.configureJwtAuthentication
import io.zinu.migaku.auth.adapter.config.loadJwtConfig
import io.zinu.migaku.auth.adapter.password.PasswordChecker
import io.zinu.migaku.auth.adapter.persist.es.document.EsRefreshTokens
import io.zinu.migaku.auth.adapter.persist.es.document.EsUsers
import io.zinu.migaku.auth.adapter.persist.es.repository.EsRefreshTokenRepository
import io.zinu.migaku.auth.adapter.persist.es.repository.EsUserRepository
import io.zinu.migaku.auth.adapter.persist.postgres.repository.PgRefreshTokenRepository
import io.zinu.migaku.auth.adapter.persist.postgres.repository.PgUserRepository
import io.zinu.migaku.auth.adapter.token.TokenGenerator
import io.zinu.migaku.auth.core.config.JwtConfig
import io.zinu.migaku.auth.core.repository.*
import io.zinu.migaku.common.adapter.config.PersistConfig
import io.zinu.migaku.common.adapter.config.PersistType
import io.zinu.migaku.common.adapter.database.ElasticsearchProvider
import io.zinu.migaku.common.adapter.database.ElasticsearchProvider.IndexCreation
import io.zinu.migaku.common.core.database.PersistTransactionPort
import org.koin.dsl.module
import org.koin.ktor.ext.inject

val authAdapterKoinModule = module {
    single {
        loadJwtConfig(get())
    }
    single<PasswordCheckerPort> { PasswordChecker }
    single<TokenGeneratorPort> { TokenGenerator(get()) }
    single<JWTVerifier> { (get<TokenGeneratorPort>() as TokenGenerator).getTokenVerifier() }
    single<UserPort> {
        when (get<PersistConfig>().persistType) {
            PersistType.POSTGRES -> PgUserRepository
            PersistType.ES -> EsUserRepository(get<PersistTransactionPort>() as ElasticsearchProvider)
        }
    }
    single<RefreshTokenPort> {
        when (get<PersistConfig>().persistType) {
            PersistType.POSTGRES -> PgRefreshTokenRepository
            PersistType.ES -> {
                EsRefreshTokenRepository(get<PersistTransactionPort>() as ElasticsearchProvider)
            }
        }
    }
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

suspend fun preInitEsRepoAuthModule(esProvider: ElasticsearchProvider) {
    esProvider.createIndexIfNotExists(
        IndexCreation(
            index = EsUsers.INDEX, mappings = EsUsers.MAPPING
        ), IndexCreation(
            index = EsRefreshTokens.INDEX, mappings = EsRefreshTokens.MAPPING
        )
    )
}

