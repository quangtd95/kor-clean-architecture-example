package io.qtd.fungpt.user.core.service

import io.qtd.fungpt.auth.core.repository.UserPort
import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.common.core.exception.UserDoesNotExistsException
import io.qtd.fungpt.user.core.usecase.UserUsecase

class UserService(
    private val userPort: UserPort,
    private val txPort: PersistTransactionPort
) : UserUsecase {

    override suspend fun getUserById(id: String) = txPort.withNewTransaction {
        userPort.getByUserId(id) ?: throw UserDoesNotExistsException()
    }

    override suspend fun getAllUsers() = txPort.withNewTransaction { userPort.getAllUsers() }
}

