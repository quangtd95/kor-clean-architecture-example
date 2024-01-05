package io.qtd.fungpt.conversation.core

import io.qtd.fungpt.common.core.bases.CoreModuleCreation
import io.qtd.fungpt.conversation.core.services.ConversationService
import io.qtd.fungpt.conversation.core.usecases.ConversationUsecase
import org.koin.dsl.module


class ConversationCoreModuleCreation : CoreModuleCreation() {
    override fun setupKoinModule() = module {
        single<ConversationUsecase> {
            ConversationService(
                conversationPort = get(), txPort = get()
            )
        }
    }
}