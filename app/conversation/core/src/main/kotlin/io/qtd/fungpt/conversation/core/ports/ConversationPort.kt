package io.qtd.fungpt.conversation.core.ports

import io.qtd.fungpt.conversation.core.models.CoreConversation
import kotlinx.coroutines.flow.Flow

interface ConversationPort {

    suspend fun createNewConversation(userId: String): CoreConversation
    suspend fun getConversations(userId: String): Flow<CoreConversation>
    suspend fun deleteConversations(userId: String): Boolean
    suspend fun deleteConversation(userId: String, conversationId: String)
    suspend fun getConversation(userId: String, conversationId: String): CoreConversation
    suspend fun updateTitleConversation(userId: String, conversationId: String, title: String): CoreConversation
}