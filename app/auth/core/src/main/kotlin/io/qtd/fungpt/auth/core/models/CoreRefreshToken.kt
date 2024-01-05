package io.qtd.fungpt.auth.core.models

import java.time.LocalDateTime


data class CoreRefreshToken(
    val token: String,
    val  userId: String,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val revoked: Boolean,
) {
    companion object
}