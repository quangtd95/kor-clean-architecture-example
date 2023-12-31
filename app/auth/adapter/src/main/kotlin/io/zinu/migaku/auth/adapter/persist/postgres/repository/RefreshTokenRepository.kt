package io.zinu.migaku.auth.adapter.persist.postgres.repository

import io.zinu.migaku.auth.adapter.persist.postgres.entity.RefreshToken
import io.zinu.migaku.auth.adapter.persist.postgres.entity.RefreshTokens
import io.zinu.migaku.auth.adapter.persist.postgres.entity.RefreshTokens.revoked
import io.zinu.migaku.auth.adapter.persist.postgres.entity.Users
import io.zinu.migaku.auth.core.repository.RefreshTokenPort
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import java.time.LocalDateTime
import java.util.*

object RefreshTokenRepository : RefreshTokenPort {

    override fun newRefreshToken(userId: String, token: String, expiredTime: LocalDateTime) = RefreshToken.new {
        this.userId = UUID.fromString(userId)
        this.token = token
        expiresAt = expiredTime
    }.toCore()

    /**
     * if token is not found or token is revoked, return false
     * otherwise return true
     */
    override fun verifyToken(token: String) =
        RefreshToken.find { RefreshTokens.token eq token and (revoked eq false) }.firstOrNull() != null

    override fun revokeAllTokens(userId: String) {
        RefreshToken.find { RefreshTokens.userId eq UUID.fromString(userId) and (revoked eq false) }
            .forEach { it.revoked = true }
    }

    override fun deleteToken(token: String) {
        RefreshToken.find { RefreshTokens.token eq token }.firstOrNull()?.delete()
    }
}