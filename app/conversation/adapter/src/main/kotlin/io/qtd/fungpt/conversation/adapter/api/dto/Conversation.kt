package io.qtd.fungpt.conversation.adapter.api.dto

import io.qtd.fungpt.conversation.core.models.CoreConversation
import java.time.format.DateTimeFormatter


data class ConversationResponse(val conversation: ConversationDto) {
    data class ConversationDto(
        val id: String, val title: String, val createdAt: String
    )

    companion object {
        fun fromCore(conversation: CoreConversation) = ConversationResponse(
            ConversationDto(
                id = conversation.id,
                title = conversation.title,
                createdAt = DateTimeFormatter.ISO_DATE_TIME.format(conversation.createdAt)
            )
        )
    }

}
