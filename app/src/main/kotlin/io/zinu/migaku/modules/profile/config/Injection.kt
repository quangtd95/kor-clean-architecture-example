package io.zinu.migaku.modules.profile.config

import io.zinu.migaku.modules.profile.service.IProfileService
import io.zinu.migaku.modules.profile.service.IUserService
import io.zinu.migaku.modules.profile.service.ProfileService
import io.zinu.migaku.modules.profile.service.UserService
import org.koin.dsl.module

val profileKoinModule = module {

    single<IProfileService> { ProfileService() }
    single<IUserService> { UserService() }
}