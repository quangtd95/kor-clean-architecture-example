package io.zinu.migaku.auth.core.repository

import io.zinu.migaku.auth.core.model.CoreRefreshToken
import java.time.LocalDateTime

interface RefreshTokenPort {
    fun newRefreshToken(userId: String, token: String, expiredTime: LocalDateTime): CoreRefreshToken
    fun verifyToken(token: String): Boolean
    fun revokeAllTokens(userId: String)
    fun deleteToken(token: String)
}

