package io.zinu.migaku.user.dto

data class UpdateUserRequest(val user: UpdateUserDto) {
    data class UpdateUserDto(
        val email: String? = null,
        val username: String? = null,
        val password: String? = null,
        val image: String? = null,
        val bio: String? = null
    )
}

data class UserResponse(val user: UserDto) {
    data class UserDto(
        val id: String,
        val email: String,
        val username: String,
        val bio: String,
        val image: String?,
    )

    companion object {
        fun fromUser(
            user: io.zinu.migaku.auth.model.User,
        ): UserResponse = UserResponse(
            UserDto(
                id = user.id.toString(),
                email = user.email, username = user.username, bio = user.bio, image = user.image
            )
        )
    }
}