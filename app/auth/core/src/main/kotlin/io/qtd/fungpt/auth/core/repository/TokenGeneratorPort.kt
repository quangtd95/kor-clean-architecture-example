package io.qtd.fungpt.auth.core.repository

import io.qtd.fungpt.auth.core.model.CoreCredentials
import io.qtd.fungpt.auth.core.model.CoreUser

interface TokenGeneratorPort {
    fun createTokens(user: CoreUser): CoreCredentials
    fun verifyToken(token: String): String?
    fun verifyRefreshToken(token: String): String?
}