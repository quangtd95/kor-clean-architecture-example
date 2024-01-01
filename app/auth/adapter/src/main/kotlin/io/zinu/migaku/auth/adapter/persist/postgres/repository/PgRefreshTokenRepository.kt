package io.zinu.migaku.auth.adapter.persist.postgres.repository

import io.zinu.migaku.auth.adapter.persist.postgres.entity.PgRefreshToken
import io.zinu.migaku.auth.adapter.persist.postgres.entity.PgRefreshTokens
import io.zinu.migaku.auth.adapter.persist.postgres.entity.PgRefreshTokens.revoked
import io.zinu.migaku.auth.core.repository.RefreshTokenPort
import org.jetbrains.exposed.sql.and
import java.time.LocalDateTime
import java.util.*

object PgRefreshTokenRepository : RefreshTokenPort {

    override suspend fun newRefreshToken(userId: String, token: String, expiredTime: LocalDateTime) =
        PgRefreshToken.new {
            this.userId = UUID.fromString(userId)
            this.token = token
            expiresAt = expiredTime
        }.toCore()

    /**
     * if token is not found or token is revoked, return false
     * otherwise return true
     */
    override suspend fun verifyToken(token: String) =
        PgRefreshToken.find { PgRefreshTokens.token eq token and (revoked eq false) }.firstOrNull() != null

    override suspend fun revokeAllTokens(userId: String) {
        PgRefreshToken.find { PgRefreshTokens.userId eq UUID.fromString(userId) and (revoked eq false) }
            .forEach { it.revoked = true }
    }

    override suspend fun deleteToken(token: String) {
        PgRefreshToken.find { PgRefreshTokens.token eq token }.firstOrNull()?.delete()
    }
}