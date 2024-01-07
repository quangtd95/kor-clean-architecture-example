package io.qtd.fungpt.auth.adapter.api.dto

import io.qtd.fungpt.auth.core.models.CoreCredentials
import io.qtd.fungpt.auth.core.models.CoreUser

data class RegisterUserRequest(val user: RegisterUserDto) {
    data class RegisterUserDto(val email: String, val password: String) {
        init {
            require(email.isNotBlank()) { "email cannot be blank" }
            require(email.contains("@")) { "email must be valid" }

            require(password.isNotBlank()) { "password cannot be blank" }
        }
    }
}

data class LoginUserRequest(val user: LoginUserDto) {
    data class LoginUserDto(val email: String, val password: String) {
        init {
            require(email.isNotBlank()) { "email cannot be blank" }
            require(email.contains("@")) { "email must be valid" }

            require(password.isNotBlank()) { "password cannot be blank" }
        }
    }
}

data class RefreshTokenRequest(val refreshToken: String) {
    init {
        require(refreshToken.isNotBlank()) { "refreshToken cannot be blank" }
    }
}

data class UserCredentialsResponse(val user: UserDto, val credentials: CredentialsDto) {
    data class UserDto(
        val id: String,
        val email: String,
    )

    data class CredentialsDto(
        val accessToken: String?,
        val accessTokenExpiredTime: String?,
        val refreshToken: String?,
        val refreshTokenExpiredTime: String?,
    )

    companion object {
        fun fromCore(user: CoreUser, credentials: CoreCredentials) =
            UserCredentialsResponse(
                UserDto(
                    id = user.id,
                    email = user.email,
                ), CredentialsDto(
                    accessToken = credentials.accessToken,
                    accessTokenExpiredTime = credentials.accessTokenExpiredTime.toString(),
                    refreshToken = credentials.refreshToken,
                    refreshTokenExpiredTime = credentials.refreshTokenExpiredTime.toString()
                )

            )
    }
}