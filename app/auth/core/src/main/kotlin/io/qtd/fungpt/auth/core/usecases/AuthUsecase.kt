package io.qtd.fungpt.auth.core.usecases

import io.qtd.fungpt.auth.core.models.CoreUserCredential

interface AuthUsecase {
    suspend fun register(email: String, password: String): CoreUserCredential
    suspend fun login(email: String, password: String): CoreUserCredential
    suspend fun refresh(refreshToken: String): CoreUserCredential
    suspend fun logout(userId: String)

    suspend fun deleteUser(userId: String)
}