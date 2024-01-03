package io.qtd.fungpt.profile.core.usecase

import io.qtd.fungpt.profile.core.model.CoreProfile


interface ProfileUsecase {
    suspend fun getProfileById(id: String): CoreProfile
    suspend fun getAllProfiles(): List<CoreProfile>
}