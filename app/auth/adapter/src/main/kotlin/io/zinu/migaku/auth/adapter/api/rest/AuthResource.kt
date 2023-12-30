package io.zinu.migaku.auth.adapter.api.rest

import io.zinu.migaku.auth.adapter.api.dto.LoginUserRequest
import io.zinu.migaku.auth.adapter.api.dto.RefreshTokenRequest
import io.zinu.migaku.auth.adapter.api.dto.RegisterUserRequest
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.zinu.migaku.auth.adapter.api.dto.UserCredentialsResponse
import io.zinu.migaku.auth.adapter.extension.userId
import io.zinu.migaku.auth.core.usecase.AuthUsecase
import io.zinu.migaku.common.adapter.base.BaseResponse.Companion.created
import io.zinu.migaku.common.adapter.base.BaseResponse.Companion.success
import io.zinu.migaku.common.adapter.base.baseRespond
import io.zinu.migaku.common.adapter.utils.Constants.JWT_AUTH
import org.koin.ktor.ext.inject

fun Route.auth() {
    val authService: AuthUsecase by inject()

    route("/auth", authDoc) {
        post("/register", registerDoc) {
            val registerUser = call.receive<RegisterUserRequest>()
            val coreUserCredential = authService.register(
                email = registerUser.user.email,
                password = registerUser.user.password
            )
            val response = UserCredentialsResponse.fromCore(
                user = coreUserCredential.user,
                credentials = coreUserCredential.credentials
            )
            call.baseRespond(created(response))
        }

        post("/login", loginDoc) {
            val loginUser = call.receive<LoginUserRequest>()
            val coreUserCredential = authService.login(
                email = loginUser.user.email,
                password = loginUser.user.password
            )

            val response = UserCredentialsResponse.fromCore(
                user = coreUserCredential.user,
                credentials = coreUserCredential.credentials
            )
            call.baseRespond(success(response))
        }

        post("/refresh", refreshDoc) {
            val refreshTokenRequest = call.receive<RefreshTokenRequest>()

            val coreUserCredential = authService.refresh(
                refreshToken = refreshTokenRequest.refreshToken
            )
            val response = UserCredentialsResponse.fromCore(
                user = coreUserCredential.user,
                credentials = coreUserCredential.credentials
            )
            call.baseRespond(success(response))
        }

        authenticate(JWT_AUTH) {
            delete("/logout", logoutDoc) {
                authService.logout(call.userId())
                call.baseRespond(success())
            }
        }

    }
}