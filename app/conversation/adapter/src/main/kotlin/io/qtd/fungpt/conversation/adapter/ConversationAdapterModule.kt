package io.qtd.fungpt.conversation.adapter

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.qtd.fungpt.common.adapter.bases.AdapterModuleCreation
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider.IndexCreation
import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.conversation.adapter.api.rest.conversations
import io.qtd.fungpt.conversation.adapter.persist.es.documents.EsConversations
import io.qtd.fungpt.conversation.adapter.persist.es.repositories.EsConversationRepository
import io.qtd.fungpt.conversation.core.ports.ConversationPort
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module


class ConversationAdapterCreation : KoinComponent, AdapterModuleCreation() {
    override suspend fun preInitDatabase() {
        val esProvider = inject<PersistTransactionPort>().value as ElasticsearchProvider
        esProvider.createIndexIfNotExists(
            IndexCreation(
                index = EsConversations.INDEX, mappings = EsConversations.MAPPING
            )
        )
    }

    override fun setupRoutingAndPlugin(app: Application) {
        with(app) {
            routing {
                route("/api") {
                    conversations()
                }
            }
        }
    }

    override fun setupKoinModule() = module {
        //TODO hardcode es type
        single<ConversationPort> { EsConversationRepository(get<PersistTransactionPort>() as ElasticsearchProvider) }
    }


}


