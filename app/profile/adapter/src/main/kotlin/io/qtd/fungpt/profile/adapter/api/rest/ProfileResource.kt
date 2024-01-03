package io.qtd.fungpt.profile.adapter.api.rest

import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.qtd.fungpt.common.adapter.base.BaseResponse.Companion.success
import io.qtd.fungpt.common.adapter.base.baseRespond
import io.qtd.fungpt.common.adapter.utils.Constants.JWT_AUTH
import io.qtd.fungpt.common.adapter.utils.param
import io.qtd.fungpt.common.adapter.utils.userId
import io.qtd.fungpt.profile.adapter.api.dto.ProfileResponse
import io.qtd.fungpt.profile.core.model.CoreProfile
import io.qtd.fungpt.profile.core.usecase.ProfileUsecase
import org.koin.ktor.ext.inject

fun Route.profile() {
    val profileUsecase: ProfileUsecase by inject()

    authenticate(JWT_AUTH) {
        route("/profiles", profilesDoc) {
            get(getListProfilesDoc) {
                val profiles: List<CoreProfile> = profileUsecase.getAllProfiles()
                val response = profiles.map(ProfileResponse.Companion::fromCore)
                call.baseRespond(success(response))
            }

            get("/{profileId}") {
                val profileId = call.param("profileId")
                val profile: CoreProfile = profileUsecase.getProfileById(profileId)
                val response = ProfileResponse.fromCore(profile)
                call.baseRespond(success(response))
            }

            route("/me") {
                get(getCurrentProfileDoc) {
                    val profile: CoreProfile = profileUsecase.getProfileById(call.userId())
                    val response = ProfileResponse.fromCore(profile)
                    call.baseRespond(success(response))
                }
            }

        }

    }
}
