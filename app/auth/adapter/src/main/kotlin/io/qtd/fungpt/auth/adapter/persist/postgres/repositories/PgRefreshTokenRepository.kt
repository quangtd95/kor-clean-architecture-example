package io.qtd.fungpt.auth.adapter.persist.postgres.repositories

import io.qtd.fungpt.auth.adapter.persist.postgres.entities.PgRefreshToken
import io.qtd.fungpt.auth.adapter.persist.postgres.entities.PgRefreshTokens
import io.qtd.fungpt.auth.adapter.persist.postgres.entities.PgRefreshTokens.revoked
import io.qtd.fungpt.auth.core.repositories.RefreshTokenPort
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