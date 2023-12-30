package io.zinu.migaku.user.core.service

import io.zinu.migaku.auth.core.repository.UserPort
import io.zinu.migaku.common.core.database.PersistTransactionPort
import io.zinu.migaku.common.core.exception.UserDoesNotExistsException
import io.zinu.migaku.user.core.usecase.UserUsecase

class UserService(
    private val userPort: UserPort,
    private val txPort: PersistTransactionPort
) : UserUsecase {

    override suspend fun getUserById(id: String) = txPort.withNewTransaction {
        userPort.getByUserId(id) ?: throw UserDoesNotExistsException()
    }

    override suspend fun getAllUsers() = txPort.withNewTransaction { userPort.getAllUsers() }
}

