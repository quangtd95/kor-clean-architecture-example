package io.qtd.fungpt.profile.adapter.api.dto

import io.qtd.fungpt.profile.core.model.CoreProfile
import java.time.format.DateTimeFormatter

data class ProfileResponse(val user: ProfileDto) {
    data class ProfileDto(
        val id: String,
        val email: String,
        val bio: String?,
        val image: String?,
        val createdAt: String,
    )

    companion object {
        fun fromCore(
            profile: CoreProfile,
        ): ProfileResponse = ProfileResponse(
            ProfileDto(
                id = profile.id,
                email = profile.email,
                bio = profile.bio,
                image = profile.avatar,
                createdAt = DateTimeFormatter.ISO_DATE_TIME.format(profile.createdAt)
            )
        )
    }
}