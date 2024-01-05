package io.qtd.fungpt.conversation.core

import io.qtd.fungpt.conversation.core.services.ConversationService
import io.qtd.fungpt.conversation.core.usecases.ConversationUsecase
import org.koin.dsl.module

val conversationCoreKoinModule = module {
    single<ConversationUsecase> {
        ConversationService(
            conversationPort = get(), txPort = get()
        )
    }
}