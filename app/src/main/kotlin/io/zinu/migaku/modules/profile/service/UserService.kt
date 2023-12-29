package io.zinu.migaku.modules.profile.service

import io.zinu.migaku.common.BaseService
import io.zinu.migaku.modules.auth.dto.UpdateUserRequest
import io.zinu.migaku.exception.UserDoesNotExistsException
import io.zinu.migaku.modules.auth.model.*
import java.util.*

interface IUserService {
    suspend fun getUserById(id: String): User
    suspend fun updateUser(id: String, updateUser: UpdateUserRequest): User
    suspend fun getAllUsers(): List<User>
}

class UserService : BaseService(), IUserService {

    override suspend fun getUserById(id: String) = dbQuery { getUser(id) }

    override suspend fun updateUser(id: String, updateUser: UpdateUserRequest) = dbQuery {
        getUser(id).apply {
            email = updateUser.user.email ?: email
            password = updateUser.user.password ?: password
            username = updateUser.user.username ?: username
            image = updateUser.user.image ?: image
            bio = updateUser.user.bio ?: bio
        }
    }

    override suspend fun getAllUsers() = dbQuery { User.all().toList() }
}

fun getUser(id: String) = User.findById(UUID.fromString(id)) ?: throw UserDoesNotExistsException()

fun getUserByUsername(username: String) = User.find { Users.username eq username }.firstOrNull()