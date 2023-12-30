package io.zinu.migaku.auth.adapter.api.rest

import io.zinu.migaku.auth.adapter.api.dto.LoginUserRequest
import io.zinu.migaku.auth.adapter.api.dto.RefreshTokenRequest
import io.zinu.migaku.auth.adapter.api.dto.RegisterUserRequest
import io.zinu.migaku.auth.adapter.api.dto.UserCredentialsResponse
import io.ktor.http.*
import io.zinu.migaku.common.base.BaseResponse
import io.zinu.migaku.common.config.ApiDoc
import io.zinu.migaku.common.config.SWAGGER_SECURITY_SCHEMA
import io.zinu.migaku.common.config.TAG_AUTH


val authDoc: ApiDoc = {
    tags = listOf(TAG_AUTH)
}
val registerDoc: ApiDoc = {
    description = "Register user"
    request {
        body(RegisterUserRequest::class)
    }
    response {
        HttpStatusCode.Created to {
            class RegisterResponseType : BaseResponse<UserCredentialsResponse>()
            body(RegisterResponseType::class)
        }
    }
}

val loginDoc: ApiDoc = {
    description = "Login user"
    request {
        body(LoginUserRequest::class)
    }
    response {
        HttpStatusCode.OK to {
            class LoginResponseType : BaseResponse<UserCredentialsResponse>()
            body(LoginResponseType::class)
        }
    }
}

val refreshDoc: ApiDoc = {
    description = "Refresh token"
    request {
        body(RefreshTokenRequest::class)
    }
    response {
        HttpStatusCode.OK to {
            class RefreshResponseType : BaseResponse<UserCredentialsResponse>()
            body(RefreshResponseType::class)
        }
    }
}

val logoutDoc: ApiDoc = {
    description = "Logout user"
    response {
        HttpStatusCode.OK to {
            class LogoutResponseType : BaseResponse<Any>()
            body(LogoutResponseType::class)
        }
    }
    securitySchemeName = SWAGGER_SECURITY_SCHEMA
}