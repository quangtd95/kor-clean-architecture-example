package io.zinu.migaku.user.service

import io.zinu.migaku.auth.dao.IUserDao
import io.zinu.migaku.auth.model.User
import io.zinu.migaku.common.base.BaseService
import io.zinu.migaku.common.exception.UserDoesNotExistsException
import org.koin.core.component.inject

interface IUserService {
    suspend fun getUserById(id: String): User

    //    suspend fun updateUser(id: String, updateUser: UpdateUserRequest): User
    suspend fun getAllUsers(): List<User>
}

class UserService : BaseService(), IUserService {
    private val userDao by inject<IUserDao>()

    override suspend fun getUserById(id: String) = dbQuery {
        userDao.getByUserId(id) ?: throw UserDoesNotExistsException()
    }

//    override suspend fun updateUser(id: String, updateUser: UpdateUserRequest) = dbQuery {
//        userDao.updateUser(id, updateUser)
//    }

    override suspend fun getAllUsers() = dbQuery { userDao.getAllUsers() }
}

