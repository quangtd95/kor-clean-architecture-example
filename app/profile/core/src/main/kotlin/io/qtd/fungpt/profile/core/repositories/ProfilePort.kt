package io.qtd.fungpt.profile.core.repositories

import io.qtd.fungpt.profile.core.models.CoreProfile


interface ProfilePort {
    suspend fun getByProfileId(profileId: String): CoreProfile?
    suspend fun getAllProfiles(): List<CoreProfile>

    suspend fun createProfile(profile: CoreProfile): CoreProfile
}

