package io.zinu.migaku.auth.core.usecase

import com.auth0.jwt.interfaces.JWTVerifier
import io.zinu.migaku.auth.core.model.CoreCredentials
import io.zinu.migaku.auth.core.model.CoreUser

interface TokenUsecase {
    fun createTokens(user: CoreUser): CoreCredentials
    fun verifyToken(token: String): String?
    fun verifyRefreshToken(token: String): String?
    fun getTokenVerifier(): JWTVerifier
}