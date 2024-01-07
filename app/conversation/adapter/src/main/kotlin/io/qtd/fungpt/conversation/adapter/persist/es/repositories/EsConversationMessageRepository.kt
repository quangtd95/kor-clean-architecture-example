package io.qtd.fungpt.conversation.adapter.persist.es.repositories

import com.jillesvangurp.ktsearch.deleteByQuery
import com.jillesvangurp.ktsearch.indexDocument
import com.jillesvangurp.ktsearch.parseHits
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.term
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider
import io.qtd.fungpt.common.core.extension.randomUUID
import io.qtd.fungpt.conversation.adapter.persist.es.documents.EsConversationsMessages
import io.qtd.fungpt.conversation.core.models.CoreConversationMessage
import io.qtd.fungpt.conversation.core.ports.ConversationMessagePort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.toKotlinLocalDateTime
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class EsConversationMessageRepository(
    private val esProvider: ElasticsearchProvider
) : ConversationMessagePort {
    private val logger = LoggerFactory.getLogger(EsConversationMessageRepository::class.java)

    override suspend fun getMessages(userId: String, conversationId: String): Flow<CoreConversationMessage> {
        return esProvider.esClient.search(target = EsConversationsMessages.INDEX) {
            query = bool {
                must(
                    term(EsConversationsMessages::userId, userId),
                    term(EsConversationsMessages::conversationId, conversationId)
                )
            }
        }.parseHits<EsConversationsMessages>().asFlow().map { it.toCore() }
    }

    override suspend fun deleteMessage(userId: String, conversationId: String, messageId: String) {
        val deleteMessageResult = esProvider.esClient.deleteByQuery(target = EsConversationsMessages.INDEX) {
            query = bool {
                must(
                    term(EsConversationsMessages::userId, userId),
                    term(EsConversationsMessages::conversationId, conversationId),
                    term(EsConversationsMessages::id, messageId)
                )
            }
        }
        logger.info("Delete message with userId: $userId, conversationId: $conversationId, messageId: $messageId, result: $deleteMessageResult")
    }

    override suspend fun deleteMessages(userId: String, conversationId: String) {
        val deleteAllMessagesResult = esProvider.esClient.deleteByQuery(target = EsConversationsMessages.INDEX) {
            query = bool {
                must(
                    term(EsConversationsMessages::userId, userId),
                    term(EsConversationsMessages::conversationId, conversationId)
                )
            }
        }
        logger.info("Delete all messages with userId: $userId, conversationId: $conversationId, result: $deleteAllMessagesResult")
    }

    override suspend fun deleteMessages(userId: String) {
        val deleteResult = esProvider.esClient.deleteByQuery(target = EsConversationsMessages.INDEX) {
            query = bool {
                must(
                    term(EsConversationsMessages::userId, userId)
                )
            }
        }
        logger.info("Delete all messages with userId: $userId, result: $deleteResult")

    }

    override suspend fun addUserMessage(
        userId: String, conversationId: String, message: String
    ): CoreConversationMessage {
        return addMessage(userId, conversationId, "user", message)
    }

    override suspend fun addAssistantMessage(
        userId: String, conversationId: String, botResponse: String
    ): CoreConversationMessage {
        return addMessage(userId, conversationId, "assistant", botResponse)
    }

    private suspend fun addMessage(
        userId: String, conversationId: String, role: String, message: String
    ): CoreConversationMessage {
        val newMessageDoc = EsConversationsMessages(
            id = randomUUID(),
            userId = userId,
            conversationId = conversationId,
            content = message,
            role = role,
            createdAt = LocalDateTime.now().toKotlinLocalDateTime(),
            modifiedAt = LocalDateTime.now().toKotlinLocalDateTime(),
            deletedAt = null,
        )
        val result = esProvider.esClient.indexDocument(
            target = EsConversationsMessages.INDEX, document = newMessageDoc
        )
        logger.info("Add user message with userId: $userId, conversationId: $conversationId, result: $result")
        return newMessageDoc.toCore()
    }
}