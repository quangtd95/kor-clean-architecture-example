package io.qtd.fungpt.user.adapter.api.rest

import io.qtd.fungpt.user.adapter.api.dto.UserResponse
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.qtd.fungpt.auth.adapter.extension.userId
import io.qtd.fungpt.auth.core.model.CoreUser
import io.qtd.fungpt.common.adapter.base.BaseResponse.Companion.success
import io.qtd.fungpt.common.adapter.base.baseRespond
import io.qtd.fungpt.common.adapter.utils.Constants.JWT_AUTH
import io.qtd.fungpt.common.adapter.utils.param
import io.qtd.fungpt.user.core.usecase.UserUsecase
import org.koin.ktor.ext.inject

fun Route.user() {
    val userService: UserUsecase by inject()

    authenticate(JWT_AUTH) {
        route("/users", usersDoc) {
            get(getListUsersDoc) {
                val users: List<CoreUser> = userService.getAllUsers()
                val response = users.map(UserResponse.Companion::fromCore)
                call.baseRespond(success(response))
            }

            get("/{userId}", getUserDoc) {
                val userId = call.param("userId")
                val user: CoreUser = userService.getUserById(userId)
                val response = UserResponse.fromCore(user)
                call.baseRespond(success(response))
            }

            route("/me") {
                get(getCurrentUserProfileDoc) {
                    val user: CoreUser = userService.getUserById(call.userId())
                    val response = UserResponse.fromCore(user)
                    call.baseRespond(success(response))
                }
            }

        }

    }
}