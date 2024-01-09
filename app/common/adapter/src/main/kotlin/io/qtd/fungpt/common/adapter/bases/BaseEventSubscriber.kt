package io.qtd.fungpt.common.adapter.bases

import io.qtd.fungpt.common.core.event.Event
import io.qtd.fungpt.common.core.event.EventSubscriberPort
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class BaseEventSubscriber<T : Event> : KoinComponent, EventSubscriber {
    protected val logger: Logger = LoggerFactory.getLogger(BaseEventSubscriber::class.java)
    private val eventSubscriberPort by inject<EventSubscriberPort>()

    abstract val topicId: String
    abstract val groupId: String
    abstract val classType: Class<T>

    abstract suspend fun handleEvent(event: T)

    override suspend fun startSubscriber() {
        eventSubscriberPort.subscribe(
            topic = topicId,
            groupId = groupId,
            eventClass = classType
        ).collect {
            logger.info("start handling event for topic  $topicId")
            handleEvent(it)
            logger.info("end handling event for topic  $topicId")
        }
    }
}

interface EventSubscriber {
    suspend fun startSubscriber()
}