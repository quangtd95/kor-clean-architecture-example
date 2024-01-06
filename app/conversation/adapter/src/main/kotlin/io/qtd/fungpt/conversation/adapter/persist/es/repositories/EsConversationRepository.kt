package io.qtd.fungpt.conversation.adapter.persist.es.repositories

import com.jillesvangurp.ktsearch.deleteByQuery
import com.jillesvangurp.ktsearch.indexDocument
import com.jillesvangurp.ktsearch.parseHits
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.searchdsls.querydsl.term
import io.qtd.fungpt.common.adapter.databases.ElasticsearchProvider
import io.qtd.fungpt.common.core.extension.randomUUID
import io.qtd.fungpt.conversation.adapter.persist.es.documents.EsConversations
import io.qtd.fungpt.conversation.core.models.CoreConversation
import io.qtd.fungpt.conversation.core.repositories.ConversationPort
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.toKotlinLocalDateTime
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class EsConversationRepository(private val esProvider: ElasticsearchProvider) : ConversationPort {

    private val logger = LoggerFactory.getLogger(EsConversationRepository::class.java)

    override suspend fun createNewConversation(userId: String): CoreConversation {
        val newConversation = EsConversations(
            id = randomUUID(),
            userId = userId,
            model = "gpt-3.5-turbo",
            title = "New conversation- ${randomUUID().takeLast(8)}",
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
            term(EsConversations::userId, userId)
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
}