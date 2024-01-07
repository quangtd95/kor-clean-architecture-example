package io.qtd.fungpt.auth.adapter.api.rest

import io.qtd.fungpt.auth.adapter.api.dto.LoginUserRequest
import io.qtd.fungpt.auth.adapter.api.dto.RefreshTokenRequest
import io.qtd.fungpt.auth.adapter.api.dto.RegisterUserRequest
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.qtd.fungpt.auth.adapter.api.dto.UserCredentialsResponse
import io.qtd.fungpt.auth.core.usecases.AuthUsecase
import io.qtd.fungpt.common.adapter.bases.BaseResponse.Companion.created
import io.qtd.fungpt.common.adapter.bases.BaseResponse.Companion.success
import io.qtd.fungpt.common.adapter.bases.baseRespond
import io.qtd.fungpt.common.adapter.utils.Constants.JWT_AUTH
import io.qtd.fungpt.common.adapter.utils.userId
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