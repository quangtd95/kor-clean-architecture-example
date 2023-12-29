package io.zinu.migaku.modules.auth.config

import com.auth0.jwt.interfaces.JWTVerifier
import io.zinu.migaku.config.ApplicationConfig
import io.zinu.migaku.modules.auth.model.IRefreshTokenDao
import io.zinu.migaku.modules.auth.model.IUserDao
import io.zinu.migaku.modules.auth.model.RefreshTokens
import io.zinu.migaku.modules.auth.model.Users
import io.zinu.migaku.modules.auth.service.*
import org.koin.dsl.module

val authKoinModule = module {
    single<ITokenService> { TokenService(get<ApplicationConfig>().jwtConfig) }
    single<JWTVerifier> { get<ITokenService>().getTokenVerifier() }
    single<IPasswordService> { PasswordService }
    single<IRefreshTokenDao> { RefreshTokens }
    single<IUserDao> { Users }
    single<IAuthService> { AuthService() }
}

