package io.qtd.fungpt.profile.core.model

data class CoreProfile(
    val id: String,
    val email: String,
    val bio: String?,
    val avatar: String?,
) {
    companion object
}