package io.qtd.fungpt.conversation.core.services

import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.conversation.core.models.ChatBotMessage
import io.qtd.fungpt.conversation.core.models.CoreConversationMessage
import io.qtd.fungpt.conversation.core.ports.ChatPort
import io.qtd.fungpt.conversation.core.ports.ConversationMessagePort
import io.qtd.fungpt.conversation.core.usecases.ConversationMessageUsecase
import kotlinx.coroutines.flow.*

class ConversationMessageService(
    private val chatPort: ChatPort,
    private val txPort: PersistTransactionPort,
    private val conversationMessagePort: ConversationMessagePort,
) : ConversationMessageUsecase {
    override suspend fun getMessages(userId: String, conversationId: String): Flow<CoreConversationMessage> {
        return txPort.withNewTransaction {
            conversationMessagePort.getMessages(userId, conversationId)
        }
    }

    override suspend fun postMessage(
        userId: String,
        conversationId: String,
        message: String
    ): Flow<CoreConversationMessage> {
        return txPort.withNewTransaction {
            flow {
                val lastMessages = conversationMessagePort.getMessages(userId, conversationId).toList()
                val userMessage = conversationMessagePort.addUserMessage(userId, conversationId, message)
                emit(userMessage)

                val assistantResponse =
                    chatPort.chat((lastMessages + userMessage).map { ChatBotMessage(it.role, it.content) })
                val assistantMessage =
                    conversationMessagePort.addAssistantMessage(userId, conversationId, assistantResponse)

                emit(assistantMessage)
            }
        }
    }

    override suspend fun postMessageStream(userId: String, conversationId: String, message: String): Flow<Any> {
        return txPort.withNewTransaction {
            flow {
                val lastMessages = conversationMessagePort.getMessages(userId, conversationId).take(2).toList()
                val userMessage = conversationMessagePort.addUserMessage(userId, conversationId, message)
                emit(userMessage)

                val assistantMessage = StringBuilder()
                val assistantStreamResponse =
                    chatPort.chatInStream((lastMessages + userMessage).map { ChatBotMessage(it.role, it.content) })

                //botStreamResponse is flow<string>, emit all strings in current flow
                assistantStreamResponse
                    .onEach {
                        assistantMessage.append(it)
                    }.onCompletion {
                        conversationMessagePort.addAssistantMessage(userId, conversationId, assistantMessage.toString())
                    }.collect {
                        emit(it)
                    }
            }
        }
    }

    override suspend fun deleteMessage(userId: String, conversationId: String, messageId: String) {
        return txPort.withNewTransaction {
            conversationMessagePort.deleteMessage(userId, conversationId, messageId)
        }
    }

    override suspend fun deleteMessages(userId: String, conversationId: String) {
        return txPort.withNewTransaction {
            conversationMessagePort.deleteMessages(userId, conversationId)
        }
    }
}