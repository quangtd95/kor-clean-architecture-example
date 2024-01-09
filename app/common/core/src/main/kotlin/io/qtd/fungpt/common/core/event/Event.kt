package io.qtd.fungpt.common.core.event

import java.time.LocalDateTime


open class Event(
    val topic: String,
    val eventType: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)


sealed class UserEvent {
    class UserCreatedEvent(val userId: String, val email: String) :
        Event(
            topic = EventType.USER_CREATED.topic,
            eventType = EventType.USER_CREATED.eventType,
            createdAt = LocalDateTime.now()
        ) {

        @Suppress("unused") // for jackson deserialization
        constructor() : this("", "")
    }

    class UserDeletedEvent(val userId: String) :
        Event(
            topic = EventType.USER_DELETED.topic,
            eventType = EventType.USER_DELETED.eventType,
            createdAt = LocalDateTime.now()
        ) {

        @Suppress("unused") // for jackson deserialization
        constructor() : this("")
    }
}

enum class EventType(
    val topic: String,
    val eventType: String,
) {
    USER_CREATED("USER_CREATED_EVENTS", "USER_CREATED_EVENTS"),
    USER_DELETED("USER_DELETED_EVENTS", "USER_DELETED_EVENTS");


}