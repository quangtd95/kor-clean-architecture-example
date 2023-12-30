package io.zinu.migaku.auth.repository

import io.zinu.migaku.auth.model.RefreshToken
import io.zinu.migaku.auth.model.RefreshTokens
import io.zinu.migaku.auth.model.RefreshTokens.revoked
import org.jetbrains.exposed.sql.and
import java.time.LocalDateTime
import java.util.*

interface IRefreshTokenRepository {
    fun newRefreshToken(userId: String, token: String, expiredTime: LocalDateTime): RefreshToken
    fun verifyToken(token: String): Boolean
    fun revokeAllTokens(userId: String)
    fun deleteToken(token: String)
}

object RefreshTokenRepository : IRefreshTokenRepository {
    override fun newRefreshToken(userId: String, token: String, expiredTime: LocalDateTime) = RefreshToken.new {
        this.userId = UUID.fromString(userId)
        this.token = token
        expiresAt = expiredTime
    }

    /**
     * if token is not found or token is revoked, return false
     * otherwise return true
     */
    override fun verifyToken(token: String): Boolean {
        var isValid = false
        RefreshToken.find { RefreshTokens.token eq token and (revoked eq false) }.firstOrNull()?.let {
            isValid = true
        }
        return isValid
    }

    override fun revokeAllTokens(userId: String) {
        RefreshToken.find { RefreshTokens.userId eq UUID.fromString(userId) and (revoked eq false) }
            .forEach { it.revoked = true }
    }

    override fun deleteToken(token: String) {
        RefreshToken.find { RefreshTokens.token eq token }.firstOrNull()?.delete()
    }
}