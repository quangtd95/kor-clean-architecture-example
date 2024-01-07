package io.qtd.fungpt.conversation.adapter.persist.es.repositories

import com.jillesvangurp.ktsearch.*
import com.jillesvangurp.searchdsls.querydsl.SortOrder
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.sort
import com.jillesvangurp.searchdsls.querydsl.term
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider
import io.qtd.fungpt.common.core.extension.randomUUID
import io.qtd.fungpt.conversation.adapter.persist.es.documents.EsConversations
import io.qtd.fungpt.conversation.core.models.CoreConversation
import io.qtd.fungpt.conversation.core.ports.ConversationPort
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.toKotlinLocalDateTime
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class EsConversationRepository(private val esProvider: ElasticsearchProvider) : ConversationPort {

    private val logger = LoggerFactory.getLogger(EsConversationRepository::class.java)

    override suspend fun createNewConversation(userId: String): CoreConversation {
        val newId = randomUUID()
        val newConversation = EsConversations(
            id = newId,
            userId = userId,
            model = "gpt-3.5-turbo",
            title = "New conversation- ${newId.takeLast(8)}",
            createdAt = LocalDateTime.now().toKotlinLocalDateTime(),
            modifiedAt = LocalDateTime.now().toKotlinLocalDateTime(),
            deletedAt = null,
        )

        val indexResponse = esProvider.esClient.indexDocument(
            target = EsConversations.INDEX,
            document = newConversation
        )
        logger.info("Create new conversation with id: ${indexResponse.id}")

        return newConversation.toCore()
    }

    override suspend fun getConversations(userId: String) = esProvider.esClient
        .search(target = EsConversations.INDEX) {
            query = term(EsConversations::userId, userId)
            sort {
                add(EsConversations::createdAt, SortOrder.DESC)
            }
        }
        .parseHits<EsConversations>()
        .asFlow()
        .map { it.toCore() }

    override suspend fun deleteConversations(userId: String): Boolean {
        val deleteResult = esProvider.esClient.deleteByQuery(target = EsConversations.INDEX) {
            query = term(EsConversations::userId, userId)
        }
        logger.info("Delete conversation with userId: $userId, result: $deleteResult")
        return true
    }

    override suspend fun deleteConversation(userId: String, conversationId: String) {
        val deleteResult = esProvider.esClient.deleteByQuery(target = EsConversations.INDEX) {
            query = bool {
                must(
                    term(EsConversations::userId, userId),
                    term(EsConversations::id, conversationId)
                )
            }
        }
        logger.info("Delete conversation with userId: $userId, conversationId: $conversationId, result: $deleteResult")
    }

    override suspend fun getConversation(userId: String, conversationId: String): CoreConversation {
        esProvider.esClient.search(target = EsConversations.INDEX) {
            query = bool {
                must(
                    term(EsConversations::userId, userId),
                    term(EsConversations::id, conversationId)
                )
            }
        }.parseHits<EsConversations>()
            .firstOrNull()
            ?.let { return it.toCore() }
            ?: throw Exception("Conversation not found")
    }

    override suspend fun updateTitleConversation(
        userId: String,
        conversationId: String,
        title: String
    ): CoreConversation {
        val conversationQuery = esProvider.esClient.search(target = EsConversations.INDEX) {
            query = term(EsConversations::id, conversationId)
        }
        val esConversation = conversationQuery.parseHits<EsConversations>()
            .firstOrNull()
            ?: throw Exception("Conversation not found")

        val esConversationId = conversationQuery.ids
            .first()

        esConversation.title = title
        esConversation.modifiedAt = LocalDateTime.now().toKotlinLocalDateTime()

        val updateResult = esProvider.esClient.updateDocument(
            target = EsConversations.INDEX,
            id = esConversationId,
            doc = esConversation
        )

        logger.info("Update conversation with userId: $userId, conversationId: $conversationId, result: $updateResult")

        esConversation.title = title
        esConversation.modifiedAt = LocalDateTime.now().toKotlinLocalDateTime()
        return esConversation.toCore()
    }
}