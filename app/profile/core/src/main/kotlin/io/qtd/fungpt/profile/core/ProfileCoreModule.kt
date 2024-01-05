package io.qtd.fungpt.profile.core

import io.qtd.fungpt.profile.core.event.NewProfileSubscriber
import io.qtd.fungpt.profile.core.service.ProfileService
import io.qtd.fungpt.profile.core.usecase.ProfileUsecase
import org.koin.dsl.module

val profileCoreKoinModule = module {

    single<ProfileUsecase> {
        ProfileService(
            profilePort = get(),
            txPort = get()
        )
    }

    single {
        NewProfileSubscriber(
            eventSubscriberPort = get(),
            persistPort = get(),
            profilePort = get()
        )
    }
}