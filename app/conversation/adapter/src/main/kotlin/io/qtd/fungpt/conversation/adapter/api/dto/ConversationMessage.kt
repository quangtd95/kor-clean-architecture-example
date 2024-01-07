package io.qtd.fungpt.conversation.adapter.api.dto

import io.qtd.fungpt.conversation.core.models.CoreConversationMessage

data class PostChat(val content: String) {
    init {
        require(content.isNotBlank()) { "content cannot be blank" }
    }
}


data class ConversationMessageResponse(val message: ConversationMessageDto) {
    data class ConversationMessageDto(
        val id: String, val role: String, val content: String, val createdAt: String
    )
}

internal fun CoreConversationMessage.toApiResponse() = ConversationMessageResponse(
    ConversationMessageResponse.ConversationMessageDto(
        id = this.id,
        role = this.role,
        content = this.content,
        createdAt = this.createdAt.toString()
    )
)