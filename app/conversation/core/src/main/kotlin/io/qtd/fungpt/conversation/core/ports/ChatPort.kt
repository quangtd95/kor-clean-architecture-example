package io.qtd.fungpt.conversation.core.ports

import io.qtd.fungpt.conversation.core.models.ChatBotMessage
import kotlinx.coroutines.flow.Flow

interface ChatPort {
    suspend fun chat(messages: List<ChatBotMessage>): String
    suspend fun chatInStream(messages: List<ChatBotMessage>): Flow<String>
    suspend fun generateTitleForConversation(messages: List<ChatBotMessage>): String
}