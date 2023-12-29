package io.zinu.migaku.modules.profile.api

import io.zinu.migaku.common.BaseResponse.Companion.success
import io.zinu.migaku.common.baseRespond
import io.zinu.migaku.modules.auth.dto.UpdateUserRequest
import io.zinu.migaku.modules.auth.dto.UserResponse
import io.zinu.migaku.modules.profile.service.IUserService
import io.zinu.migaku.utils.Constants.JWT_AUTH
import io.zinu.migaku.utils.userId
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.put
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.user() {
    val userService: IUserService by inject()

    authenticate(JWT_AUTH) {
        route("/users", usersDoc) {
            get(getListUsersDoc) {
                val users = userService.getAllUsers()
                val response = users.map { UserResponse.fromUser(it) }
                call.baseRespond(success(response))
            }

            route("/me") {
                get(getCurrentUserProfileDoc) {
                    val user = userService.getUserById(call.userId())
                    val response = UserResponse.fromUser(user)
                    call.baseRespond(success(response))
                }

                put(updateCurrentUserProfileDoc) {
                    val updateUser = call.receive<UpdateUserRequest>()
                    val user = userService.updateUser(call.userId(), updateUser)
                    val response = UserResponse.fromUser(user)
                    call.baseRespond(success(response))
                }
            }

        }

    }
}