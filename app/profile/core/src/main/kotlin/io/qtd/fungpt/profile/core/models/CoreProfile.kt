package io.qtd.fungpt.profile.core.models

import java.time.LocalDateTime

data class CoreProfile(
    val id: String,
    val email: String,
    val bio: String?,
    val avatar: String?,
    val createdAt: LocalDateTime,
) {
    companion object
}