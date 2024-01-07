package io.qtd.fungpt.profile.adapter.events

import io.qtd.fungpt.common.adapter.bases.BaseEventSubscriber
import io.qtd.fungpt.common.core.event.EventType
import io.qtd.fungpt.common.core.event.UserEvent
import io.qtd.fungpt.profile.core.usecases.ProfileUsecase

class UserDeletedEventsSubscriber(
    private val profilePort: ProfileUsecase,
) : BaseEventSubscriber<UserEvent.UserDeletedEvent>() {

    override val topicId: String = EventType.USER_DELETED.topic
    override val groupId: String = "profile-module-subscribe-user-deleted-events"
    override val classType = UserEvent.UserDeletedEvent::class.java

    override suspend fun handleEvent(event: UserEvent.UserDeletedEvent) {
        event.let {
            logger.info("delete profile event: $it")
            profilePort.deleteProfile(it.userId)
        }
    }
}