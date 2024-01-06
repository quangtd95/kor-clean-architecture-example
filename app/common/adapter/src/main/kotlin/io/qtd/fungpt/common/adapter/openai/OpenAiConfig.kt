package io.qtd.fungpt.common.adapter.openai

import io.ktor.server.config.*

data class OpenAiConfig(
    val token: String, val model: String
)

class OpenAiConfigBuilder {
    lateinit var token: String
    lateinit var model: String
    fun build(): OpenAiConfig = OpenAiConfig(token, model)
}

fun openai(block: OpenAiConfigBuilder.() -> Unit): OpenAiConfig {
    return OpenAiConfigBuilder().apply(block).build()
}

fun loadOpenAIConfig(hoconConfig: HoconApplicationConfig) = openai {
    with(hoconConfig.config("openai")) {
        token = property("token").getString()
        model = property("model").getString()
    }
}