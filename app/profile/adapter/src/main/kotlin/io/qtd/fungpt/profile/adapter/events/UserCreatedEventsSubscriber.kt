package io.qtd.fungpt.profile.adapter.events

import io.qtd.fungpt.common.adapter.bases.BaseEventSubscriber
import io.qtd.fungpt.common.core.event.EventType
import io.qtd.fungpt.common.core.event.UserEvent
import io.qtd.fungpt.profile.core.usecases.ProfileUsecase

class UserCreatedEventsSubscriber(
    private val profileUsecase: ProfileUsecase
) :
    BaseEventSubscriber<UserEvent.UserCreatedEvent>() {

    override val topicId = EventType.USER_CREATED.topic
    override val groupId = "profile-module-subscribe-user-created-events"
    override val classType = UserEvent.UserCreatedEvent::class.java

    override suspend fun handleEvent(event: UserEvent.UserCreatedEvent) {
        event.let {
            logger.info("New profile event: $it")
            profileUsecase.createNewProfile(it.userId, it.email)
        }
    }

}