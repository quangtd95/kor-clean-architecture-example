package io.zinu.migaku.modules.conversation.dto

import io.zinu.migaku.modules.conversation.model.Conversation
import io.zinu.migaku.modules.conversation.model.ConversationMessage

data class PostChat(val content: String) {
    init {
        require(content.isNotBlank()) { "content cannot be blank" }
    }
}

data class ConversationResponse(val conversation: ConversationDto) {
    data class ConversationDto(
        val id: String, val title: String, val createdAt: String
    )

    companion object {
        fun fromConversation(conversation: Conversation) = ConversationResponse(
            ConversationDto(
                id = conversation.id.toString(),
                title = conversation.title ?: "",
                createdAt = conversation.createdAt.toString()
            )
        )
    }

}

data class ConversationMessageStreamResponse(val message: ConversationMessageStreamDto) {
    data class ConversationMessageStreamDto(val id: String, val chunk: String, val end: Boolean)
}

data class ConversationMessageResponse(val message: ConversationMessageDto) {
    data class ConversationMessageDto(
        val id: String, val role: String, val content: String, val createdAt: String
    )

    companion object {
        fun fromConversationMessage(conversationMessage: ConversationMessage) = ConversationMessageResponse(
            ConversationMessageDto(
                id = conversationMessage.id.toString(),
                role = conversationMessage.role,
                content = conversationMessage.content,
                createdAt = conversationMessage.createdAt.toString()
            )
        )
    }

}