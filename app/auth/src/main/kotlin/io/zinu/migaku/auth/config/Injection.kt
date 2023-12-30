package io.zinu.migaku.auth.config

import com.auth0.jwt.interfaces.JWTVerifier
import io.zinu.migaku.auth.repository.IRefreshTokenRepository
import io.zinu.migaku.auth.repository.IUserRepository
import io.zinu.migaku.auth.repository.RefreshTokenRepository
import io.zinu.migaku.auth.repository.UserRepository
import io.zinu.migaku.auth.service.*
import io.zinu.migaku.common.config.CommonConfig
import org.koin.dsl.module

val authKoinModule = module {
    single<ITokenService> { TokenService(get<CommonConfig>().jwtConfig) }
    single<JWTVerifier> { get<ITokenService>().getTokenVerifier() }
    single<IPasswordService> { PasswordService }
    single<IRefreshTokenRepository> { RefreshTokenRepository }
    single<IUserRepository> { UserRepository }
    single<IAuthService> { AuthService() }
}

