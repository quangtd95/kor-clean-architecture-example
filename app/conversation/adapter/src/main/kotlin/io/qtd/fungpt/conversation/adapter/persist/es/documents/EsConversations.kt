package io.qtd.fungpt.conversation.adapter.persist.es.documents

import com.jillesvangurp.searchdsls.mappingdsl.IndexSettingsAndMappingsDSL
import io.qtd.fungpt.common.adapter.bases.EsBaseIdDocument
import io.qtd.fungpt.conversation.core.models.CoreConversation
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class EsConversations(
    val id: String,
    var model: String,
    var title: String,
    val userId: String,
    val createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime,
    var deletedAt: LocalDateTime?,
) : EsBaseIdDocument() {
    companion object {
        const val INDEX = "conversation_conversations"

        val MAPPING = IndexSettingsAndMappingsDSL().apply {
            mappings(dynamicEnabled = true) {
                keyword(EsConversations::id)
                keyword(EsConversations::model)
                text(EsConversations::title)
                keyword(EsConversations::userId)
                date(EsConversations::createdAt)
                date(EsConversations::modifiedAt)
                date(EsConversations::deletedAt)
            }
        }
    }

    fun toCore(): CoreConversation {
        return CoreConversation(
            id = id,
            userId = userId,
            model = model,
            title = title,
            createdAt = createdAt.toJavaLocalDateTime(),
            modifiedAt = modifiedAt.toJavaLocalDateTime(),
            deletedAt = deletedAt?.toJavaLocalDateTime(),
        )
    }
}