package io.qtd.fungpt.conversation.core.services

import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.conversation.core.models.ChatBotMessage
import io.qtd.fungpt.conversation.core.models.CoreConversation
import io.qtd.fungpt.conversation.core.ports.ChatPort
import io.qtd.fungpt.conversation.core.ports.ConversationMessagePort
import io.qtd.fungpt.conversation.core.ports.ConversationPort
import io.qtd.fungpt.conversation.core.usecases.ConversationUsecase
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList

class ConversationService(
    private val conversationPort: ConversationPort,
    private val conversationMessagePort: ConversationMessagePort,
    private val chatPort: ChatPort,
    private val txPort: PersistTransactionPort,
) : ConversationUsecase {

    override suspend fun createConversation(userId: String) = txPort.withNewTransaction {
        conversationPort.createNewConversation(userId)
    }

    override suspend fun getConversations(userId: String) = txPort.withNewTransaction {
        conversationPort.getConversations(userId)
    }

    override suspend fun deleteConversations(userId: String) = txPort.withNewTransaction {
        conversationPort.deleteConversations(userId)
        conversationMessagePort.deleteMessages(userId)
    }

    override suspend fun deleteConversation(userId: String, conversationId: String) = txPort.withNewTransaction {
        conversationPort.deleteConversation(userId, conversationId)
        conversationMessagePort.deleteMessages(userId, conversationId)
    }

    override suspend fun getConversation(userId: String, conversationId: String): CoreConversation {
        return txPort.withNewTransaction {
            conversationPort.getConversation(userId, conversationId)
        }
    }

    override suspend fun generateTitleConversation(userId: String, conversationId: String) : CoreConversation {
        return txPort.withNewTransaction {
            val userMessageList = conversationMessagePort
                .getMessages(userId, conversationId)
                .filter { it.role == "user" }
                .toList()
            val title = chatPort.generateTitleForConversation(
                messages = userMessageList.map {
                    ChatBotMessage(
                        role = it.role,
                        content = it.content,
                    )
                }
            )
            conversationPort.updateTitleConversation(userId, conversationId, title)
        }
    }


}