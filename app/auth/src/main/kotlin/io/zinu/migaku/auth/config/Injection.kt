package io.zinu.migaku.auth.config

import com.auth0.jwt.interfaces.JWTVerifier
import io.zinu.migaku.auth.dao.IRefreshTokenDao
import io.zinu.migaku.auth.dao.IUserDao
import io.zinu.migaku.auth.dao.RefreshTokenDao
import io.zinu.migaku.auth.dao.UserDao
import io.zinu.migaku.auth.service.*
import io.zinu.migaku.common.config.CommonConfig
import io.zinu.migaku.common.config.JwtConfig
import org.koin.dsl.module

val authKoinModule = module {
    single<ITokenService> { TokenService(get<CommonConfig>().jwtConfig) }
    single<JWTVerifier> { get<ITokenService>().getTokenVerifier() }
    single<IPasswordService> { PasswordService }
    single<IRefreshTokenDao> { RefreshTokenDao }
    single<IUserDao> { UserDao }
    single<IAuthService> { AuthService() }
}

