package io.qtd.fungpt.common.core.event

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface EventPublisherPort {
    suspend fun <T> publish(event: Event<T>)
}

interface EventSubscriberPort {
    suspend fun <T> subscribe(eventType: EventType): Flow<Event<T>>
}

class Event<T>(
    val type: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val data: T,
) {
    companion object {
        fun <T> of(type: EventType, data: T) = Event(type.name, data = data)

    }
}

enum class EventType {
    USER_CREATED,
}