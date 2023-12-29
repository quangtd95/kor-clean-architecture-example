package com.qtd.modules.auth.api

import com.qtd.common.BaseResponse.Companion.created
import com.qtd.common.BaseResponse.Companion.success
import com.qtd.modules.auth.dto.LoginUserRequest
import com.qtd.modules.auth.dto.RefreshTokenRequest
import com.qtd.modules.auth.dto.RegisterUserRequest
import com.qtd.modules.auth.service.IAuthService
import com.qtd.common.baseRespond
import com.qtd.utils.Constants.JWT_AUTH
import com.qtd.utils.userId
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.auth() {
    val authService: IAuthService by inject()

    route("/auth", authDoc) {
        post("/register", registerDoc) {
            val registerUser = call.receive<RegisterUserRequest>()
            val newUser = authService.register(registerUser)
            call.baseRespond(created(newUser))
        }

        post("/login", loginDoc) {
            val loginUser = call.receive<LoginUserRequest>()
            val user = authService.login(loginUser.user.email, loginUser.user.password)
            call.baseRespond(success(user))
        }

        post("/refresh", refreshDoc) {
            val refreshTokenRequest = call.receive<RefreshTokenRequest>()
            val userCredentials = authService.refresh(refreshTokenRequest.refreshToken)
            call.baseRespond(success(userCredentials))
        }

        authenticate(JWT_AUTH) {
            delete("/logout", logoutDoc) {
                authService.logout(call.userId())
                call.baseRespond(success())
            }
        }

    }
}