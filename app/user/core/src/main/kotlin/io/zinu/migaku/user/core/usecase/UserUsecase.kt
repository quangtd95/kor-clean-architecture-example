package io.zinu.migaku.user.core.usecase

import io.zinu.migaku.auth.core.model.CoreUser

interface UserUsecase {
    suspend fun getUserById(id: String): CoreUser
    suspend fun getAllUsers(): List<CoreUser>
}