package io.qtd.fungpt.common.core.event

import kotlinx.coroutines.flow.Flow

interface EventPublisherPort {
    suspend fun <T : Event> publish(e: T)
}

interface EventSubscriberPort {
    suspend fun <T : Event> subscribe(topic: String, groupId: String, eventClass: Class<T>): Flow<T>
}
