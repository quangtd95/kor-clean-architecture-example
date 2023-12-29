package io.zinu.migaku.modules.conversation.config

import io.zinu.migaku.modules.conversation.service.ConversationService
import org.koin.dsl.module

val conversationKoinModule = module {
    single<ConversationService> { ConversationService }
}