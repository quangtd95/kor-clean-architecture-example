package io.qtd.fungpt.auth.core

import io.qtd.fungpt.auth.core.services.AuthService
import io.qtd.fungpt.auth.core.usecases.AuthUsecase
import org.koin.dsl.module

val authCoreKoinModule = module {

    single<AuthUsecase> {
        AuthService(
            passwordChecker = get(),
            tokenGenerator = get(),
            refreshTokenPort = get(),
            userPort = get(),
            txPort = get(),
            eventPublisherPort = get()
        )
    }
}
