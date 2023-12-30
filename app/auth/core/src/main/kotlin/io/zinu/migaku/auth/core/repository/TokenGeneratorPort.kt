package io.zinu.migaku.auth.core.repository

import io.zinu.migaku.auth.core.model.CoreCredentials
import io.zinu.migaku.auth.core.model.CoreUser

interface TokenGeneratorPort {
    fun createTokens(user: CoreUser): CoreCredentials
    fun verifyToken(token: String): String?
    fun verifyRefreshToken(token: String): String?
}