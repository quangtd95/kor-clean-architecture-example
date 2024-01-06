package io.qtd.fungpt.conversation.core.services

import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.conversation.core.models.CoreConversation
import io.qtd.fungpt.conversation.core.repositories.ConversationPort
import io.qtd.fungpt.conversation.core.usecases.ConversationUsecase
import kotlinx.coroutines.flow.Flow

class ConversationService(
    private val conversationPort: ConversationPort,
    private val txPort: PersistTransactionPort,
) : ConversationUsecase {

    override suspend fun createConversation(userId: String) = txPort.withNewTransaction {
        conversationPort.createNewConversation(userId)
    }

    override suspend fun getConversations(userId: String): Flow<CoreConversation> {
        return conversationPort.getConversations(userId)
    }

}