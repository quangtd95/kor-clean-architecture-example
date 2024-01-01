package io.qtd.fungpt.user.core.usecase

import io.qtd.fungpt.auth.core.model.CoreUser

interface UserUsecase {
    suspend fun getUserById(id: String): CoreUser
    suspend fun getAllUsers(): List<CoreUser>
}