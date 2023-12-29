package io.zinu.migaku.modules.profile.service

import io.zinu.migaku.common.BaseService
import io.zinu.migaku.exception.UserDoesNotExistsException
import io.zinu.migaku.modules.auth.dao.IUserDao
import io.zinu.migaku.modules.auth.model.User
import io.zinu.migaku.modules.profile.dto.UpdateUserRequest
import org.koin.core.component.inject

interface IUserService {
    suspend fun getUserById(id: String): User
    suspend fun updateUser(id: String, updateUser: UpdateUserRequest): User
    suspend fun getAllUsers(): List<User>
}

class UserService : BaseService(), IUserService {
    private val userDao by inject<IUserDao>()

    override suspend fun getUserById(id: String) = dbQuery {
        userDao.getByUserId(id) ?: throw UserDoesNotExistsException()
    }

    override suspend fun updateUser(id: String, updateUser: UpdateUserRequest) = dbQuery {
        userDao.updateUser(id, updateUser)
    }

    override suspend fun getAllUsers() = dbQuery { userDao.getAllUsers() }
}

