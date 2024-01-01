package io.zinu.migaku.auth.core.repository

import io.zinu.migaku.auth.core.model.CoreUser


interface UserPort {
    suspend fun createNewUser(email: String, password: String): CoreUser
    suspend fun isExists(email: String): Boolean
    suspend fun getByUserId(userId: String): CoreUser?
    suspend fun getByEmail(email: String): CoreUser?
    suspend fun getAllUsers(): List<CoreUser>
}

