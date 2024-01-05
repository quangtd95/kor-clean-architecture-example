package io.qtd.fungpt.profile.adapter

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider.IndexCreation
import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.profile.adapter.persist.es.documents.EsProfiles
import io.qtd.fungpt.profile.adapter.persist.es.repositories.EsProfileRepository
import io.qtd.fungpt.profile.adapter.api.rest.profile
import io.qtd.fungpt.profile.core.repositories.ProfilePort
import org.koin.dsl.module
import org.koin.ktor.ext.inject

val profileAdapterKoinModule = module {
    single<ProfilePort> {
        EsProfileRepository(get<PersistTransactionPort>() as ElasticsearchProvider)
    }
}

fun Application.profileModule() {
    routing {
        route("/api") {
            profile()
        }
    }
}

suspend fun Application.preInitEsRepoProfileModule() {
    val esProvider = (inject<PersistTransactionPort>().value as ElasticsearchProvider)
    esProvider.createIndexIfNotExists(
        IndexCreation(index = EsProfiles.INDEX, mappings = EsProfiles.MAPPING)
    )
}
