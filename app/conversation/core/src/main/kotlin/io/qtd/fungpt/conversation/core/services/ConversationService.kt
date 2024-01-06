package io.qtd.fungpt.conversation.core.services

import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.conversation.core.models.CoreConversation
import io.qtd.fungpt.conversation.core.ports.ConversationPort
import io.qtd.fungpt.conversation.core.usecases.ConversationUsecase

class ConversationService(
    private val conversationPort: ConversationPort,
    private val txPort: PersistTransactionPort,
) : ConversationUsecase {

    override suspend fun createConversation(userId: String) = conversationPort.createNewConversation(userId)

    override suspend fun getConversations(userId: String) = conversationPort.getConversations(userId)

    override suspend fun deleteConversations(userId: String) = conversationPort.deleteConversations(userId)

    override suspend fun deleteConversation(userId: String, conversationId: String) =
        conversationPort.deleteConversation(userId, conversationId)

    override suspend fun getConversation(userId: String, conversationId: String): CoreConversation {
        return conversationPort.getConversation(userId, conversationId)
    }

}