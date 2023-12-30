package io.zinu.migaku.user.core

import io.zinu.migaku.user.core.service.UserService
import io.zinu.migaku.user.core.usecase.UserUsecase
import org.koin.dsl.module

val userCoreKoinModule = module {

    single<UserUsecase> {
        UserService(
            userPort = get(),
            txPort = get()
        )
    }
}