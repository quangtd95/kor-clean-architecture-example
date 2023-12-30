package io.zinu.migaku.auth.core

import io.zinu.migaku.auth.core.service.AuthService
import io.zinu.migaku.auth.core.usecase.AuthUsecase
import org.koin.dsl.module

val authCoreKoinModule = module {

    single<AuthUsecase> {
        AuthService(
            passwordService = get(),
            tokenService = get(),
            refreshTokenPort = get(),
            userPort = get(),
            txPort = get()
        )
    }
}
