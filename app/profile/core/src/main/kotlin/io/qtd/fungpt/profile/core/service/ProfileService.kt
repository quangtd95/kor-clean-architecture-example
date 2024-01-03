package io.qtd.fungpt.profile.core.service

import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.common.core.exception.UserDoesNotExistsException
import io.qtd.fungpt.profile.core.repository.ProfilePort
import io.qtd.fungpt.profile.core.usecase.ProfileUsecase

class ProfileService(
    private val profilePort: ProfilePort,
    private val txPort: PersistTransactionPort
) : ProfileUsecase {

    override suspend fun getProfileById(id: String) = txPort.withNewTransaction {
        profilePort.getByProfileId(id) ?: throw UserDoesNotExistsException()
    }

    override suspend fun getAllProfiles() = txPort.withNewTransaction { profilePort.getAllProfiles() }
}

