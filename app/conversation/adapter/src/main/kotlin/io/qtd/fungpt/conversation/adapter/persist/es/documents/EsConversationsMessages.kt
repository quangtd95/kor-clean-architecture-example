package io.qtd.fungpt.conversation.adapter.persist.es.documents

import com.jillesvangurp.searchdsls.mappingdsl.IndexSettingsAndMappingsDSL
import io.qtd.fungpt.common.adapter.bases.EsBaseIdDocument
import io.qtd.fungpt.conversation.core.models.CoreConversationMessage
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class EsConversationsMessages(
    val id: String,
    val userId: String,
    val conversationId: String,
    val content: String,
    val role: String,
    val createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime,
    var deletedAt: LocalDateTime?,
) : EsBaseIdDocument() {
    companion object {
        const val INDEX = "conversation_conversations_messages"

        val MAPPING = IndexSettingsAndMappingsDSL().apply {
            mappings(dynamicEnabled = true) {
                keyword(EsConversationsMessages::id)
                keyword(EsConversationsMessages::userId)
                keyword(EsConversationsMessages::conversationId)
                text(EsConversationsMessages::content)
                keyword(EsConversationsMessages::role)
                date(EsConversationsMessages::createdAt)
                date(EsConversationsMessages::modifiedAt)
                date(EsConversationsMessages::deletedAt)
            }
        }
    }

    fun toCore(): CoreConversationMessage {
        return CoreConversationMessage(
            id = id,
            userId = userId,
            conversationId = conversationId,
            content = content,
            role = role,
            createdAt = createdAt.toJavaLocalDateTime(),
            modifiedAt = modifiedAt.toJavaLocalDateTime(),
            deletedAt = deletedAt?.toJavaLocalDateTime(),
        )
    }
}