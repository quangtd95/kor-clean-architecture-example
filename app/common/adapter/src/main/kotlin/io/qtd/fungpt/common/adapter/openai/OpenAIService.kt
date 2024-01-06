package io.qtd.fungpt.common.adapter.openai

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import kotlin.time.Duration.Companion.seconds


interface IOpenAIChatService {
    suspend fun chat(messages: List<Message>): String
    suspend fun chatStream(messages: List<Message>): Flow<String>
    suspend fun generateTitleForConversation(messages: List<Message>): String
}


class OpenAIService(private val openAiConfig: OpenAiConfig) : KoinComponent, IOpenAIChatService {
    private val openAI = getOpenAIInstance(openAiConfig)

    override suspend fun chat(messages: List<Message>): String {
        return sendPrompt(messages, system_instructions_chat)
    }

    override suspend fun generateTitleForConversation(messages: List<Message>): String {
        return sendPrompt(messages, system_instructions_generate_title)
    }

    override suspend fun chatStream(messages: List<Message>): Flow<String> {
        val result = openAI.chatCompletions(
            ChatCompletionRequest(model = ModelId(openAiConfig.model), messages = listOf(
                ChatMessage(role = ChatRole.System, content = system_instructions_chat)
            ) + messages.map { ChatMessage(role = fromString(it.role), content = it.content) })
        )
        return result.map {
            it.choices[0].delta.content ?: ""
        }
    }


    private suspend fun sendPrompt(messages: List<Message>, systemInstruction: String): String {
        val systemMessage = ChatMessage(role = ChatRole.System, content = systemInstruction)
        val chatMessages = messages.map { ChatMessage(role = fromString(it.role), content = it.content) }
        val sendMessages = listOf(systemMessage) + chatMessages

        val result = openAI.chatCompletion(
            ChatCompletionRequest(
                model = ModelId(openAiConfig.model),
                messages = sendMessages
            )
        )
        return if (result.choices.isNotEmpty()) {
            result.choices[0].message.content ?: "Sorry I don't understand"
        } else {
            "Sorry I don't understand"
        }
    }

    private fun getOpenAIInstance(openAiConfig: OpenAiConfig) = OpenAI(
        OpenAIConfig(
            token = openAiConfig.token,
            logging = LoggingConfig(
                logLevel = LogLevel.All,
                sanitize = true
            ),
            timeout = Timeout(60.seconds)
        )
    )
}

val system_instructions_generate_title = """
    You are a fun assistant, your name is Fun-GPT, you can answer everything concisely. 
    You just need to generate a title that encapsulates the essence of our conversation so far.
    Maximum length of the title is 60 characters.
""".trimIndent()

val system_instructions_chat = """
    You are a fun assistant, your name is Fun-GPT, you can answer everything concisely. 
    At the end of each answer, add a fun fact or a short joke. 
    Use many icons with a humorous style.
""".trimIndent()

fun fromString(role: String): ChatRole = when (role) {
    "system" -> ChatRole.System
    "user" -> ChatRole.User
    "assistant" -> ChatRole.Assistant
    else -> ChatRole.User
}
