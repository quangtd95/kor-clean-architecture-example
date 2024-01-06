package io.qtd.fungpt.common.adapter.openai

import io.ktor.server.config.*

data class OpenAIConfig(
    val token: String, val model: String
)

fun loadOpenAIConfig(hoconConfig: HoconApplicationConfig): OpenAIConfig {
    with(hoconConfig.config("openai")) {
        return OpenAIConfig(
            token = property("token").getString(),
            model = property("model").getString()
        )
    }
}