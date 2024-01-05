package io.qtd.fungpt.auth.adapter

import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.qtd.fungpt.auth.adapter.api.rest.auth
import io.qtd.fungpt.auth.adapter.configs.configureJwtAuthentication
import io.qtd.fungpt.auth.adapter.configs.loadJwtConfig
import io.qtd.fungpt.auth.adapter.password.PasswordChecker
import io.qtd.fungpt.auth.adapter.persist.es.documents.EsRefreshTokens
import io.qtd.fungpt.auth.adapter.persist.es.documents.EsUsers
import io.qtd.fungpt.auth.adapter.persist.es.repositories.EsRefreshTokenRepository
import io.qtd.fungpt.auth.adapter.persist.es.repositories.EsUserRepository
import io.qtd.fungpt.auth.adapter.persist.postgres.entities.PgRefreshTokens
import io.qtd.fungpt.auth.adapter.persist.postgres.entities.PgUsers
import io.qtd.fungpt.auth.adapter.persist.postgres.repositories.PgRefreshTokenRepository
import io.qtd.fungpt.auth.adapter.persist.postgres.repositories.PgUserRepository
import io.qtd.fungpt.auth.adapter.token.TokenGenerator
import io.qtd.fungpt.auth.core.configs.JwtConfig
import io.qtd.fungpt.auth.core.repositories.PasswordCheckerPort
import io.qtd.fungpt.auth.core.repositories.RefreshTokenPort
import io.qtd.fungpt.auth.core.repositories.TokenGeneratorPort
import io.qtd.fungpt.auth.core.repositories.UserPort
import io.qtd.fungpt.common.adapter.databases.config.PersistConfig
import io.qtd.fungpt.common.adapter.databases.config.PersistType
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider.IndexCreation
import io.qtd.fungpt.common.core.database.PersistTransactionPort
import org.jetbrains.exposed.sql.SchemaUtils
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

suspend fun Application.preInitPostgresRepoAuthModule() {
    SchemaUtils.createMissingTablesAndColumns(PgUsers, PgRefreshTokens)
}

suspend fun Application.preInitEsRepoAuthModule() {
    val esProvider = (inject<PersistTransactionPort>().value as ElasticsearchProvider)
    esProvider.createIndexIfNotExists(
        IndexCreation(
            index = EsUsers.INDEX, mappings = EsUsers.MAPPING
        ), IndexCreation(
            index = EsRefreshTokens.INDEX, mappings = EsRefreshTokens.MAPPING
        )
    )
}

