package io.zinu.migaku.auth.adapter.persist.es.document

import io.zinu.migaku.auth.core.model.CoreRefreshToken
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
//TODO missing mapping for this document when creating the index
data class EsRefreshTokens(
    val id: String,
    val token: String,
    val userId: String,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val revoked: Boolean,
) {


    companion object {
        const val INDEX = "refresh_tokens"
    }

    fun toCore() = CoreRefreshToken(
        token = token,
        userId = userId,
        expiresAt = expiresAt.toJavaLocalDateTime(),
        createdAt = createdAt.toJavaLocalDateTime(),
        revoked = revoked
    )
}