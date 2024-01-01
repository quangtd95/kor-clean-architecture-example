package io.qtd.fungpt.user.core

import io.qtd.fungpt.user.core.service.UserService
import io.qtd.fungpt.user.core.usecase.UserUsecase
import org.koin.dsl.module

val userCoreKoinModule = module {

    single<UserUsecase> {
        UserService(
            userPort = get(),
            txPort = get()
        )
    }
}