package io.qtd.fungpt.conversation.core.usecases

import io.qtd.fungpt.conversation.core.models.CoreConversation

interface ConversationUsecase {
    suspend fun createConversation(userId: String): CoreConversation


}