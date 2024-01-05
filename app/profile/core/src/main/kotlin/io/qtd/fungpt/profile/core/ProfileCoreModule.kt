package io.qtd.fungpt.profile.core

import io.qtd.fungpt.profile.core.events.NewProfileSubscriber
import io.qtd.fungpt.profile.core.services.ProfileService
import io.qtd.fungpt.profile.core.usecases.ProfileUsecase
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