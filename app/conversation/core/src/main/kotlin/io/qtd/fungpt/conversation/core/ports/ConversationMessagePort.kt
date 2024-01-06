package io.qtd.fungpt.conversation.core.ports

import io.qtd.fungpt.conversation.core.models.CoreConversationMessage
import kotlinx.coroutines.flow.Flow

interface ConversationMessagePort {
    suspend fun getMessages(userId: String, conversationId: String): Flow<CoreConversationMessage>
    suspend fun deleteMessage(userId: String, conversationId: String, messageId: String)
    suspend fun deleteMessages(userId: String, conversationId: String)
    suspend fun deleteMessages(userId: String)
    suspend fun addUserMessage(userId: String, conversationId: String, message: String): CoreConversationMessage
    suspend fun addAssistantMessage(userId: String, conversationId: String, botResponse: String): CoreConversationMessage
}