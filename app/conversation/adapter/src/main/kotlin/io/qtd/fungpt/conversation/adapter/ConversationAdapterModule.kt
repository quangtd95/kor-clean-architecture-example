package io.qtd.fungpt.conversation.adapter

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.qtd.fungpt.common.adapter.bases.AdapterModuleCreation
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider.IndexCreation
import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.conversation.adapter.api.rest.conversations
import io.qtd.fungpt.conversation.adapter.api.rest.conversationsMessages
import io.qtd.fungpt.conversation.adapter.chat.ChatBotAdapter
import io.qtd.fungpt.conversation.adapter.persist.es.documents.EsConversations
import io.qtd.fungpt.conversation.adapter.persist.es.documents.EsConversationsMessages
import io.qtd.fungpt.conversation.adapter.persist.es.repositories.EsConversationMessageRepository
import io.qtd.fungpt.conversation.adapter.persist.es.repositories.EsConversationRepository
import io.qtd.fungpt.conversation.core.ports.ChatPort
import io.qtd.fungpt.conversation.core.ports.ConversationMessagePort
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
            ),
            IndexCreation(
                index = EsConversationsMessages.INDEX, mappings = EsConversationsMessages.MAPPING
            )
        )
    }

    override fun setupRoutingAndPlugin(app: Application) {
        with(app) {
            routing {
                route("/api") {
                    conversations()
                    conversationsMessages()
                }
            }
        }
    }

    override fun setupKoinModule() = module {
        //TODO hardcode es type
        single<ConversationPort> { EsConversationRepository(get<PersistTransactionPort>() as ElasticsearchProvider) }
        single<ConversationMessagePort> {
            EsConversationMessageRepository(get<PersistTransactionPort>() as ElasticsearchProvider)
        }
        single<ChatPort> {
            ChatBotAdapter(get())
        }

    }
}