package io.qtd.fungpt.conversation.core.usecases

import io.qtd.fungpt.conversation.core.models.CoreConversationMessage
import kotlinx.coroutines.flow.Flow

interface ConversationMessageUsecase {
    /**
     * get all messages from a conversation
     */
    suspend fun getMessages(userId: String, conversationId: String): Flow<CoreConversationMessage>

    /**
     * submit a new user message, then response with the user message and the bot message
     */
    suspend fun postMessage(userId: String, conversationId: String, message: String): Flow<CoreConversationMessage>

    /**
     * submit a new user message, then response with the first element is user message,
     * the rest is bot message stream in words
     */
    suspend fun postMessageStream(
        userId: String, conversationId: String, message: String
    ): Flow<Any>

    /**
     *  delete a message
     */
    suspend fun deleteMessage(userId: String, conversationId: String, messageId: String)

    /**
     * delete all messages from a conversation
     */
    suspend fun deleteMessages(userId: String, conversationId: String)
}