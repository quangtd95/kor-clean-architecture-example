package io.qtd.fungpt.profile.adapter

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.qtd.fungpt.common.adapter.bases.AdapterModuleCreation
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider.IndexCreation
import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.profile.adapter.api.rest.profile
import io.qtd.fungpt.profile.adapter.persist.es.documents.EsProfiles
import io.qtd.fungpt.profile.adapter.persist.es.repositories.EsProfileRepository
import io.qtd.fungpt.profile.core.ports.ProfilePort
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

class ProfileAdapterModuleCreation : KoinComponent, AdapterModuleCreation() {
    override suspend fun preInitDatabase() {
        val esProvider = (inject<PersistTransactionPort>().value as ElasticsearchProvider)
        esProvider.createIndexIfNotExists(
            IndexCreation(index = EsProfiles.INDEX, mappings = EsProfiles.MAPPING)
        )
    }

    override fun setupRoutingAndPlugin(app: Application) {
        with(app) {
            routing {
                route("/api") {
                    profile()
                }
            }
        }

    }

    override fun setupKoinModule() = module {
        single<ProfilePort> {
            EsProfileRepository(get<PersistTransactionPort>() as ElasticsearchProvider)
        }
    }
}

