package io.qtd.fungpt.common.adapter.event

import com.fasterxml.jackson.databind.json.JsonMapper
import io.github.nomisRev.kafka.Acks
import io.github.nomisRev.kafka.KafkaProducer
import io.github.nomisRev.kafka.ProducerSettings
import io.github.nomisRev.kafka.imap
import io.qtd.fungpt.common.core.event.Event
import io.qtd.fungpt.common.core.event.EventPublisherPort
import io.qtd.fungpt.common.core.extension.randomUUID
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

class KafkaEventPublisher : EventPublisherPort {

    override suspend fun <T> publish(event: Event<T>) {
        val settings = ProducerSettings<String, Event<T>>(
            bootstrapServers = "localhost:29092",
            keyDeserializer = StringSerializer(),
            valueDeserializer = StringSerializer().imap {
                JsonMapper.builder()
                    .findAndAddModules()
                    .build()
                    .writeValueAsString(it)
            },
            acks = Acks.Zero
        )
        val kafkaPublisher = KafkaProducer(settings)
        kafkaPublisher.send(ProducerRecord("test", randomUUID(), event))
    }
}

