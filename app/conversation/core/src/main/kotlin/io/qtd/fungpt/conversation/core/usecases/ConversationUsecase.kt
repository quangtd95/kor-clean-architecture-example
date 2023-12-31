package io.qtd.fungpt.conversation.core.usecases

import io.qtd.fungpt.conversation.core.models.CoreConversation
import kotlinx.coroutines.flow.Flow

interface ConversationUsecase {
    suspend fun createConversation(userId: String): CoreConversation
    suspend fun getConversations(userId: String): Flow<CoreConversation>
    suspend fun deleteConversations(userId: String)
    suspend fun deleteConversation(userId: String, conversationId: String)
    suspend fun getConversation(userId: String, conversationId: String): CoreConversation
    suspend fun generateTitleConversation(userId: String, conversationId: String): CoreConversation
}