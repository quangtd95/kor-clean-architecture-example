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
import io.qtd.fungpt.auth.core.ports.PasswordCheckerPort
import io.qtd.fungpt.auth.core.ports.RefreshTokenPort
import io.qtd.fungpt.auth.core.ports.TokenGeneratorPort
import io.qtd.fungpt.auth.core.ports.UserPort
import io.qtd.fungpt.common.adapter.bases.AdapterModuleCreation
import io.qtd.fungpt.common.adapter.bases.EventSubscriber
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider.IndexCreation
import io.qtd.fungpt.common.adapter.databases.config.PersistConfig
import io.qtd.fungpt.common.adapter.databases.config.PersistType
import io.qtd.fungpt.common.core.database.PersistTransactionPort
import org.jetbrains.exposed.sql.SchemaUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

class AuthAdapterModuleCreation : KoinComponent, AdapterModuleCreation() {
    override suspend fun preInitDatabase() {
        val persistType = inject<PersistConfig>().value.persistType
        when (persistType) {
            PersistType.POSTGRES -> preInitPostgresRepoAuthModule()
            PersistType.ES -> {
                val esProvider = (inject<PersistTransactionPort>().value as ElasticsearchProvider)
                preInitEsRepoAuthModule(esProvider)
            }
        }
    }

    override fun setupApiAndPlugin(app: Application) {
        with(app) {
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

    }

    override fun setupKoinModule() = module {
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

    override fun getEventSubscriber(): List<EventSubscriber> = listOf()

    private fun preInitPostgresRepoAuthModule() {
        SchemaUtils.createMissingTablesAndColumns(PgUsers, PgRefreshTokens)
    }

    private suspend fun preInitEsRepoAuthModule(esProvider: ElasticsearchProvider) {
        esProvider.createIndexIfNotExists(
            IndexCreation(
                index = EsUsers.INDEX, mappings = EsUsers.MAPPING
            ), IndexCreation(
                index = EsRefreshTokens.INDEX, mappings = EsRefreshTokens.MAPPING
            )
        )
    }

}


