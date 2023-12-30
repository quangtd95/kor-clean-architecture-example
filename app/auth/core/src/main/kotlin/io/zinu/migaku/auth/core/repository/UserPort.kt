package io.zinu.migaku.auth.core.repository

import io.zinu.migaku.auth.core.model.CoreUser


interface UserPort {
    fun createNewUser(email: String, password: String): CoreUser
    fun isExists(email: String): Boolean
    fun getByUserId(userId: String): CoreUser?
    fun getByEmail(email: String): CoreUser?
    fun getAllUsers(): List<CoreUser>
}

