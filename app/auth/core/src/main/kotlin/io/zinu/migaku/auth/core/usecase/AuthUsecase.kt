package io.zinu.migaku.auth.core.usecase

import io.zinu.migaku.auth.core.model.CoreUserCredential

interface AuthUsecase {
    suspend fun register(email: String, password: String): CoreUserCredential
    suspend fun login(email: String, password: String): CoreUserCredential
    suspend fun refresh(refreshToken: String): CoreUserCredential
    suspend fun logout(userId: String)
}