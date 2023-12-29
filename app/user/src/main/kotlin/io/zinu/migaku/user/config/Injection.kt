package io.zinu.migaku.user.config

import io.zinu.migaku.user.service.IUserService
import io.zinu.migaku.user.service.UserService
import org.koin.dsl.module

val userKoinModule = module {

    single<IUserService> { UserService() }
}