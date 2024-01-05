package io.qtd.fungpt.auth.core.repositories

import io.qtd.fungpt.auth.core.models.CoreCredentials
import io.qtd.fungpt.auth.core.models.CoreUser

interface TokenGeneratorPort {
    fun createTokens(user: CoreUser): CoreCredentials
    fun verifyToken(token: String): String?
    fun verifyRefreshToken(token: String): String?
}