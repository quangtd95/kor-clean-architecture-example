package io.zinu.migaku.modules.auth.config

import com.auth0.jwt.interfaces.JWTVerifier
import io.zinu.migaku.config.ApplicationConfig
import io.zinu.migaku.modules.auth.dao.IRefreshTokenDao
import io.zinu.migaku.modules.auth.dao.IUserDao
import io.zinu.migaku.modules.auth.dao.RefreshTokenDao
import io.zinu.migaku.modules.auth.dao.UserDao
import io.zinu.migaku.modules.auth.service.*
import org.koin.dsl.module

val authKoinModule = module {
    single<ITokenService> { TokenService(get<ApplicationConfig>().jwtConfig) }
    single<JWTVerifier> { get<ITokenService>().getTokenVerifier() }
    single<IPasswordService> { PasswordService }
    single<IRefreshTokenDao> { RefreshTokenDao }
    single<IUserDao> { UserDao }
    single<IAuthService> { AuthService() }
}

