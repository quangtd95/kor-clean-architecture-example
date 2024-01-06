package io.qtd.fungpt.conversation.core.services

import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.conversation.core.ports.ConversationPort
import io.qtd.fungpt.conversation.core.usecases.ConversationUsecase

class ConversationService(
    private val conversationPort: ConversationPort,
    private val txPort: PersistTransactionPort,
) : ConversationUsecase {

    override suspend fun createConversation(userId: String) = conversationPort.createNewConversation(userId)

    override suspend fun getConversations(userId: String) = conversationPort.getConversations(userId)

    override suspend fun deleteConversations(userId: String) = conversationPort.deleteConversations(userId)

}