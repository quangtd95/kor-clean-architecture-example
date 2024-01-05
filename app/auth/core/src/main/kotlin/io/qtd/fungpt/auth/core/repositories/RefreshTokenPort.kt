package io.qtd.fungpt.auth.core.repositories

import io.qtd.fungpt.auth.core.models.CoreRefreshToken
import java.time.LocalDateTime

interface RefreshTokenPort {
    suspend fun newRefreshToken(userId: String, token: String, expiredTime: LocalDateTime): CoreRefreshToken
    suspend fun verifyToken(token: String): Boolean
    suspend fun revokeAllTokens(userId: String)
    suspend fun deleteToken(token: String)
}

