package io.qtd.fungpt.profile.core.repository

import io.qtd.fungpt.profile.core.model.CoreProfile


interface ProfilePort {
    suspend fun getByProfileId(profileId: String): CoreProfile?
    suspend fun getAllProfiles(): List<CoreProfile>

    suspend fun createProfile(profile: CoreProfile): CoreProfile
}

