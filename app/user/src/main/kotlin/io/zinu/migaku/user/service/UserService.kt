package io.zinu.migaku.user.service

import io.zinu.migaku.auth.repository.IUserRepository
import io.zinu.migaku.auth.model.User
import io.zinu.migaku.common.base.BaseService
import io.zinu.migaku.common.exception.UserDoesNotExistsException
import org.koin.core.component.inject

interface IUserService {
    suspend fun getUserById(id: String): User
    suspend fun getAllUsers(): List<User>
}

class UserService : BaseService(), IUserService {
    private val userRepository by inject<IUserRepository>()

    override suspend fun getUserById(id: String) = dbQuery {
        userRepository.getByUserId(id) ?: throw UserDoesNotExistsException()
    }

    override suspend fun getAllUsers() = dbQuery { userRepository.getAllUsers() }
}

