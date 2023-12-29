package io.zinu.migaku.user.api

import io.zinu.migaku.common.base.BaseResponse.Companion.success
import io.zinu.migaku.user.dto.UserResponse
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.zinu.migaku.common.base.baseRespond
import io.zinu.migaku.common.utils.Constants.JWT_AUTH
import io.zinu.migaku.common.utils.param
import io.zinu.migaku.common.utils.userId
import io.zinu.migaku.user.service.IUserService
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

            get("/{userId}", getUserDoc) {
                val userId = call.param("userId")
                val user = userService.getUserById(userId)
                val response = UserResponse.fromUser(user)
                call.baseRespond(success(response))
            }

            route("/me") {
                get(getCurrentUserProfileDoc) {
                    val user = userService.getUserById(call.userId())
                    val response = UserResponse.fromUser(user)
                    call.baseRespond(success(response))
                }

//                put(updateCurrentUserProfileDoc) {
//                    val updateUser = call.receive<UpdateUserRequest>()
//                    val user = userService.updateUser(call.userId(), updateUser)
//                    val response = UserResponse.fromUser(user)
//                    call.baseRespond(success(response))
//                }
            }

        }

    }
}