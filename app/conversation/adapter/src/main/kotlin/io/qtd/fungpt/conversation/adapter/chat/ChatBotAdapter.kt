package io.qtd.fungpt.conversation.adapter.chat

import io.qtd.fungpt.common.adapter.openai.IOpenAIChatService
import io.qtd.fungpt.common.adapter.openai.Message
import io.qtd.fungpt.conversation.core.models.ChatBotMessage
import io.qtd.fungpt.conversation.core.ports.ChatPort
import kotlinx.coroutines.flow.Flow

class ChatBotAdapter(private val openAIChatService: IOpenAIChatService) : ChatPort {
    override suspend fun chat(messages: List<ChatBotMessage>): String {
        return openAIChatService.chat(messages.map { Message(it.role, it.content) })
    }

    override suspend fun chatInStream(messages: List<ChatBotMessage>): Flow<String> {
        return openAIChatService.chatStream(messages.map {
                Message(it.role, it.content)
            })
    }
}