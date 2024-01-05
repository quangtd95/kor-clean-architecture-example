package io.qtd.fungpt.conversation.core.repositories

import io.qtd.fungpt.conversation.core.models.CoreConversation

interface ConversationPort {
    suspend fun createNewConversation(userId: String): CoreConversation
}