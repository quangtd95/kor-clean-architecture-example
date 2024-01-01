package io.zinu.migaku.auth.adapter.persist.postgres.entity

import io.zinu.migaku.auth.core.model.CoreRefreshToken
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

object PgRefreshTokens : UUIDTable("refresh_tokens") {
    val token = varchar("token", 1000).uniqueIndex()
    val userId = uuid("user_id").references(PgUsers.id)
    val expiresAt = datetime("expires_at")
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val revoked = bool("revoked").default(false)
}

class PgRefreshToken(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PgRefreshToken>(PgRefreshTokens)

    var token by PgRefreshTokens.token
    var userId by PgRefreshTokens.userId
    var expiresAt by PgRefreshTokens.expiresAt
    var createdAt by PgRefreshTokens.createdAt
    var revoked by PgRefreshTokens.revoked

    fun toCore() = CoreRefreshToken(
        token = token,
        userId = userId.toString(),
        expiresAt = expiresAt,
        createdAt = createdAt,
        revoked = revoked
    )
}