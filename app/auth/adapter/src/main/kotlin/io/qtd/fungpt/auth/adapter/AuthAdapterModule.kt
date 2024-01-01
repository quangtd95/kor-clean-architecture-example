package io.qtd.fungpt.auth.adapter

import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.qtd.fungpt.auth.adapter.api.rest.auth
import io.qtd.fungpt.auth.adapter.config.configureJwtAuthentication
import io.qtd.fungpt.auth.adapter.config.loadJwtConfig
import io.qtd.fungpt.auth.adapter.password.PasswordChecker
import io.qtd.fungpt.auth.adapter.persist.es.document.EsRefreshTokens
import io.qtd.fungpt.auth.adapter.persist.es.document.EsUsers
import io.qtd.fungpt.auth.adapter.persist.es.repository.EsRefreshTokenRepository
import io.qtd.fungpt.auth.adapter.persist.es.repository.EsUserRepository
import io.qtd.fungpt.auth.adapter.persist.postgres.repository.PgRefreshTokenRepository
import io.qtd.fungpt.auth.adapter.persist.postgres.repository.PgUserRepository
import io.qtd.fungpt.auth.adapter.token.TokenGenerator
import io.qtd.fungpt.auth.core.config.JwtConfig
import io.qtd.fungpt.auth.core.repository.*
import io.qtd.fungpt.common.adapter.config.PersistConfig
import io.qtd.fungpt.common.adapter.config.PersistType
import io.qtd.fungpt.common.adapter.database.ElasticsearchProvider
import io.qtd.fungpt.common.adapter.database.ElasticsearchProvider.IndexCreation
import io.qtd.fungpt.common.core.database.PersistTransactionPort
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

