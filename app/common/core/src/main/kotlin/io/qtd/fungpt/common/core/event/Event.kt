package io.qtd.fungpt.common.core.event

import java.time.LocalDateTime


open class Event(
    val topic: String,
    val eventType: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)

sealed class UserEvent {
    class UserCreatedEvent(val userId: String, val email: String) :
        Event("USER_EVENTS", EventType.USER_CREATED.name, LocalDateTime.now())
}

enum class EventType {
    USER_CREATED,
}