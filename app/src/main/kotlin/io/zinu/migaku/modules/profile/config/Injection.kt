package io.zinu.migaku.modules.profile.config

import io.zinu.migaku.modules.profile.service.IUserService
import io.zinu.migaku.modules.profile.service.UserService
import org.koin.dsl.module

val profileKoinModule = module {

    single<IUserService> { UserService() }
}