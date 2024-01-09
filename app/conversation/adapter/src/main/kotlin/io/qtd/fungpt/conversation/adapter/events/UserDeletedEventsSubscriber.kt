package io.qtd.fungpt.conversation.adapter.events

import io.qtd.fungpt.common.adapter.bases.BaseEventSubscriber
import io.qtd.fungpt.common.core.event.EventType
import io.qtd.fungpt.common.core.event.UserEvent
import io.qtd.fungpt.conversation.core.usecases.ConversationUsecase

class UserDeletedEventsSubscriber(
    private val conversationUsecase: ConversationUsecase
) : BaseEventSubscriber<UserEvent.UserDeletedEvent>() {
    override val groupId = "conversation-module-subscribe-user-delete-events"
    override val topicId = EventType.USER_DELETED.topic
    override val classType = UserEvent.UserDeletedEvent::class.java

    override suspend fun handleEvent(event: UserEvent.UserDeletedEvent) {
        event.let {
            conversationUsecase.deleteConversations(it.userId)
        }
    }

}