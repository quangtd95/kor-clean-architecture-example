package io.qtd.fungpt.profile.core.events

import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.common.core.event.EventSubscriberPort
import io.qtd.fungpt.common.core.event.EventType
import io.qtd.fungpt.common.core.event.UserEvent
import io.qtd.fungpt.profile.core.models.CoreProfile
import io.qtd.fungpt.profile.core.ports.ProfilePort
import org.slf4j.LoggerFactory

class NewProfileSubscriber(
    private val eventSubscriberPort: EventSubscriberPort,
    private val persistPort: PersistTransactionPort,
    private val profilePort: ProfilePort
) {
    private val logger = LoggerFactory.getLogger(NewProfileSubscriber::class.java)

    suspend fun subscribeUserEvents() {
        eventSubscriberPort.subscribe(
            topic = "USER_EVENTS",
            groupId = "profile-module-subscribe-user-events",
            eventClass = UserEvent.UserCreatedEvent::class.java
        ).collect {

            persistPort.withNewTransaction {

                logger.info("New profile event: $it")

                if (it.eventType == EventType.USER_CREATED.name) {
                    profilePort.createProfile(
                        CoreProfile(
                            id = it.userId,
                            email = it.email,
                            bio = "bio",
                            avatar = "avatar",
                            createdAt = it.createdAt
                        )
                    )
                }

            }

        }
    }

}