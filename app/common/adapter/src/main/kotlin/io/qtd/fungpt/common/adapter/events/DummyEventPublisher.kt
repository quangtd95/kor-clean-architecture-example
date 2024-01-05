package io.qtd.fungpt.common.adapter.events

import io.qtd.fungpt.common.core.event.Event
import io.qtd.fungpt.common.core.event.EventPublisherPort
import org.slf4j.LoggerFactory

class DummyEventPublisher : EventPublisherPort {
    private val logger = LoggerFactory.getLogger(DummyEventPublisher::class.java)

    override suspend fun <T : Event> publish(e: T) {
        logger.info("Publish event: $e")
    }
}