package io.zinu.migaku.auth.core

import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.zinu.migaku.auth.core.service.AuthService
import io.zinu.migaku.auth.core.service.IPasswordService
import io.zinu.migaku.auth.core.service.PasswordService
import io.zinu.migaku.auth.core.service.TokenService
import io.zinu.migaku.auth.core.usecase.AuthUsecase
import io.zinu.migaku.auth.core.usecase.TokenUsecase
import org.koin.dsl.module

val authCoreKoinModule = module {
    single<TokenUsecase> {
        TokenService(
            jwtConfig = get())
    }
    single<JWTVerifier> {
        get<TokenUsecase>().getTokenVerifier()
    }
    single<IPasswordService> {
        PasswordService
    }
    single<AuthUsecase> {
        AuthService(
            passwordService = get(),
            tokenService = get(),
            refreshTokenPort = get(),
            userPort = get(),
            txPort = get()
        )
    }
}
