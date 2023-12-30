package io.zinu.migaku.user.adapter.api.dto

import io.zinu.migaku.auth.core.model.CoreUser

data class UserResponse(val user: UserDto) {
    data class UserDto(
        val id: String,
        val email: String,
        val bio: String?,
        val image: String?,
    )

    companion object {
        fun fromCore(
            user: CoreUser,
        ): UserResponse = UserResponse(
            UserDto(
                id = user.id,
                email = user.email, bio = user.bio, image = user.image
            )
        )
    }
}