package io.qtd.fungpt.profile.core.usecases

import io.qtd.fungpt.profile.core.models.CoreProfile


interface ProfileUsecase {
    suspend fun getProfileById(id: String): CoreProfile
    suspend fun getAllProfiles(): List<CoreProfile>

    suspend fun createNewProfile(userId: String, email: String): CoreProfile
    suspend fun deleteProfile(userId: String)
}