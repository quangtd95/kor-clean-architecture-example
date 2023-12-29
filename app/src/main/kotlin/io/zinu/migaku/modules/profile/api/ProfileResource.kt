package io.zinu.migaku.modules.profile.api

import io.zinu.migaku.common.BaseResponse.Companion.success
import io.zinu.migaku.common.baseRespond
import io.zinu.migaku.modules.profile.service.IProfileService
import io.zinu.migaku.utils.Constants.JWT_AUTH
import io.zinu.migaku.utils.userId
import io.zinu.migaku.utils.userIdOrNull
import io.zinu.migaku.utils.username
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.profile() {
    val profileService: IProfileService by inject()

    route("/profiles", profilesDoc) {
        route("/{username}") {
            authenticate(JWT_AUTH, optional = true) {
                get(getProfileDoc) {
                    val username = call.username()
                    val currentUserId = call.userIdOrNull()
                    val profile = profileService.getProfile(username, currentUserId)
                    call.baseRespond(success(profile))
                }
            }

            authenticate(JWT_AUTH) {
                route("/follow", followDoc) {
                    post(postFollowProfileDoc) {
                        val username = call.username()
                        val currentUserId = call.userId()
                        val profile = profileService.changeFollowStatus(username, currentUserId, true)
                        call.baseRespond(success(profile))
                    }

                    delete(deleteFollowProfileDoc) {
                        val username = call.username()
                        val currentUserId = call.userId()
                        val profile = profileService.changeFollowStatus(username, currentUserId, false)
                        call.baseRespond(success(profile))
                    }
                }

            }
        }

    }

}