package io.qtd.fungpt.conversation.core.services

import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.conversation.core.repositories.ConversationPort
import io.qtd.fungpt.conversation.core.usecases.ConversationUsecase

class ConversationService(
    private val conversationPort: ConversationPort,
    private val txPort: PersistTransactionPort,
) : ConversationUsecase {

    override suspend fun createConversation(userId: String) = txPort.withNewTransaction {
        conversationPort.createNewConversation(userId)
    }

}