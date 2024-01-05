package io.qtd.fungpt.auth.core.repositories

import io.qtd.fungpt.auth.core.models.CoreUser


interface UserPort {
    suspend fun createNewUser(email: String, password: String): CoreUser
    suspend fun isExists(email: String): Boolean
    suspend fun getByUserId(userId: String): CoreUser?
    suspend fun getByEmail(email: String): CoreUser?
    suspend fun getAllUsers(): List<CoreUser>
}

