package io.qtd.fungpt.common.adapter.event.config

import io.ktor.server.config.*

data class KafkaConfig(
    val bootstrapServer: String
)

class KafkaConfigBuilder {
    var bootstrapServer: String = "localhost:9092"

    fun build(): KafkaConfig = KafkaConfig(bootstrapServer)
}

fun kafkaConfig(block: KafkaConfigBuilder.() -> Unit): KafkaConfig =
    KafkaConfigBuilder().apply(block).build()


fun loadKafkaConfig(hoconConfig: HoconApplicationConfig) = kafkaConfig {
    with(hoconConfig.config("event.kafka")) {
        bootstrapServer = property("bootstrapServer").getString()
    }
}