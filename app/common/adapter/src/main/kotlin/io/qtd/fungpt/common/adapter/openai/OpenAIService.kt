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
    suspend fun chat(message: List<Message>): String
    suspend fun chatStream(message: List<Message>): Flow<String>
}


class OpenAIService(private val openAiConfig: io.qtd.fungpt.common.adapter.openai.OpenAIConfig) : KoinComponent, IOpenAIChatService {
    private val openAI = getOpenAIInstance(openAiConfig)

    override suspend fun chat(message: List<Message>): String {
        val result = openAI.chatCompletion(
            ChatCompletionRequest(model = ModelId(openAiConfig.model), messages = listOf(
                ChatMessage(role = ChatRole.System, content = system_instructions)
            ) + message.map { ChatMessage(role = fromString(it.role), content = it.content) })
        )
        return if (result.choices.isNotEmpty()) {
            result.choices[0].message.content ?: "Sorry I don't understand"
        } else {
            "Sorry I don't understand"
        }
    }

    override suspend fun chatStream(message: List<Message>): Flow<String> {
        val result = openAI.chatCompletions(
            ChatCompletionRequest(model = ModelId(openAiConfig.model), messages = listOf(
                ChatMessage(role = ChatRole.System, content = system_instructions)
            ) + message.map { ChatMessage(role = fromString(it.role), content = it.content) })
        )
        return result.map {
            it.choices[0].delta.content ?: ""
        }
    }

    private fun getOpenAIInstance(openAiConfig: io.qtd.fungpt.common.adapter.openai.OpenAIConfig) = OpenAI(
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

val system_instructions = """
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
