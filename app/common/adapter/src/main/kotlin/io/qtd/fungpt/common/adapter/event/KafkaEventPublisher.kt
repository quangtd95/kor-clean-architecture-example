package io.qtd.fungpt.common.adapter.event

import com.fasterxml.jackson.databind.json.JsonMapper
import io.github.nomisRev.kafka.Acks
import io.github.nomisRev.kafka.KafkaProducer
import io.github.nomisRev.kafka.ProducerSettings
import io.github.nomisRev.kafka.imap
import io.qtd.fungpt.common.adapter.event.config.KafkaConfig
import io.qtd.fungpt.common.core.event.Event
import io.qtd.fungpt.common.core.event.EventPublisherPort
import io.qtd.fungpt.common.core.extension.randomUUID
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

class KafkaEventPublisher(private val kafkaConfig: KafkaConfig) : EventPublisherPort {

    private val jsonMapper = JsonMapper.builder().findAndAddModules().build()

    private val producerSettings: ProducerSettings<String, Event> by lazy {
        ProducerSettings<String, Event>(
            bootstrapServers = kafkaConfig.bootstrapServer,
            keyDeserializer = StringSerializer(),
            valueDeserializer = StringSerializer().imap { jsonMapper.writeValueAsString(it) },
            acks = Acks.All
        )
    }
    private val kafkaPublisher = KafkaProducer(producerSettings)

    override suspend fun <T : Event> publish(e: T) {
        kafkaPublisher.send(ProducerRecord(e.topic, randomUUID(), e))
    }
}

