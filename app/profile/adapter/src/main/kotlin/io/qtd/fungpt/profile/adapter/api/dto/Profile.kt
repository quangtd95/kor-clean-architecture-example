package io.qtd.fungpt.profile.adapter.api.dto

import io.qtd.fungpt.profile.core.model.CoreProfile

data class ProfileResponse(val user: ProfileDto) {
    data class ProfileDto(
        val id: String,
        val email: String,
        val bio: String?,
        val image: String?,
    )

    companion object {
        fun fromCore(
            profile: CoreProfile,
        ): ProfileResponse = ProfileResponse(
            ProfileDto(
                id = profile.id,
                email = profile.email, bio = profile.bio, image = profile.avatar
            )
        )
    }
}