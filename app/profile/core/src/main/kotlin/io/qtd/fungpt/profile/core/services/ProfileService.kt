package io.qtd.fungpt.profile.core.services

import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.common.core.exception.UserDoesNotExistsException
import io.qtd.fungpt.profile.core.models.CoreProfile
import io.qtd.fungpt.profile.core.ports.ProfilePort
import io.qtd.fungpt.profile.core.usecases.ProfileUsecase
import java.time.LocalDateTime

class ProfileService(
    private val profilePort: ProfilePort,
    private val txPort: PersistTransactionPort
) : ProfileUsecase {

    override suspend fun getProfileById(id: String) = txPort.withNewTransaction {
        profilePort.getByProfileId(id) ?: throw UserDoesNotExistsException()
    }

    override suspend fun getAllProfiles() = txPort.withNewTransaction { profilePort.getAllProfiles() }

    override suspend fun createNewProfile(userId: String, email: String) = txPort.withNewTransaction {
        profilePort.createProfile(
            CoreProfile(
                id = userId,
                email = email,
                bio = "bio",
                avatar = "avatar",
                createdAt = LocalDateTime.now()
            )
        )

    }

    override suspend fun deleteProfile(userId: String) = txPort.withNewTransaction {
        profilePort.deleteProfile(
            profileId = userId
        )
    }
}

