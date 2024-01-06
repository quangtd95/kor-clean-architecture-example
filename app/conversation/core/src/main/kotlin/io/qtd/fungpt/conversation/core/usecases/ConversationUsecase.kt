package io.qtd.fungpt.conversation.core.usecases

import io.qtd.fungpt.conversation.core.models.CoreConversation
import kotlinx.coroutines.flow.Flow

interface ConversationUsecase {
    suspend fun createConversation(userId: String): CoreConversation
    suspend fun getConversations(userId: String): Flow<CoreConversation>
    suspend fun deleteConversations(userId: String): Boolean


}