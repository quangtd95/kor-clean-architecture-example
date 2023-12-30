package io.zinu.migaku.auth.core.model

import java.time.LocalDateTime

data class CoreUserCredential(
    val user: CoreUser,
    val credentials: CoreCredentials,
)

data class CoreUser(
    val id: String,
    val email: String,
    val bio: String?,
    val image: String?,
    val password: String,
) {
    companion object
}

data class CoreCredentials(
    val accessToken: String,
    val accessTokenExpiredTime: LocalDateTime,
    val refreshToken: String,
    val refreshTokenExpiredTime: LocalDateTime,
)