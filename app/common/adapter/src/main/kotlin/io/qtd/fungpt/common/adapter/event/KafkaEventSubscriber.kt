package io.qtd.fungpt.common.adapter.event

import com.fasterxml.jackson.databind.json.JsonMapper
import io.github.nomisRev.kafka.map
import io.github.nomisRev.kafka.receiver.KafkaReceiver
import io.github.nomisRev.kafka.receiver.ReceiverSettings
import io.qtd.fungpt.common.adapter.event.config.KafkaConfig
import io.qtd.fungpt.common.core.event.Event
import io.qtd.fungpt.common.core.event.EventSubscriberPort
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory

class KafkaEventSubscriber(private val kafkaConfig: KafkaConfig) : EventSubscriberPort {

    private val jsonMapper = JsonMapper.builder().findAndAddModules().build()
    private val logger = LoggerFactory.getLogger(KafkaEventSubscriber::class.java)


    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun <T : Event> subscribe(topic: String, groupId: String, eventClass: Class<T>): Flow<T> {
        return KafkaReceiver(
            ReceiverSettings(
                bootstrapServers = kafkaConfig.bootstrapServer,
                keyDeserializer = StringDeserializer(),
                valueDeserializer = StringDeserializer().map {
                    jsonMapper.readValue(it, eventClass)
                },
                groupId = groupId,
            )
        ).receiveAutoAck(topic)
            .cancellable()
            .flatMapConcat { flowOfConsumerRecord ->
                flowOfConsumerRecord
                    .onEach { logger.info("$groupId - Received event from [$topic]: $it") }
                    .map { it.value() }
            }

    }
}