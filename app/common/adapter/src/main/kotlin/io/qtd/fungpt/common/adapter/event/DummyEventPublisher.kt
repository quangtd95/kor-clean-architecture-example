package io.qtd.fungpt.common.adapter.event

import io.qtd.fungpt.common.core.event.Event
import io.qtd.fungpt.common.core.event.EventPublisherPort
import org.slf4j.LoggerFactory

class DummyEventPublisher : EventPublisherPort {
    private val logger = LoggerFactory.getLogger(DummyEventPublisher::class.java)

    override suspend fun <T> publish(event: Event<T>) {
        logger.info("Publish event: $event")
    }
}