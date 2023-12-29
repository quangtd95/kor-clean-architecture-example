package io.zinu.migaku.modules.profile.api

import io.zinu.migaku.config.ApiDoc
import io.zinu.migaku.config.SWAGGER_SECURITY_SCHEMA
import io.zinu.migaku.config.TAG_USER
import io.zinu.migaku.common.BaseResponse
import io.zinu.migaku.modules.auth.dto.UpdateUserRequest
import io.zinu.migaku.modules.auth.dto.UserResponse
import io.ktor.http.*

val usersDoc: ApiDoc = {
    tags = listOf(TAG_USER)
    securitySchemeName = SWAGGER_SECURITY_SCHEMA
}

val getListUsersDoc: ApiDoc = {
    description = "Get list users"
    response {
        HttpStatusCode.OK to {
            class GetListUsersDocType : BaseResponse<List<UserResponse>>()
            body(GetListUsersDocType::class)
        }
    }
}

val getCurrentUserProfileDoc: ApiDoc = {
    description = "Get current user profile"
    response {
        HttpStatusCode.OK to {
            class GetCurrentUserProfileDocType : BaseResponse<UserResponse>()
            body(GetCurrentUserProfileDocType::class)
        }
    }
}

val updateCurrentUserProfileDoc: ApiDoc = {
    description = "Update current user profile"
    request {
        body(UpdateUserRequest::class)
    }
    response {
        HttpStatusCode.OK to {
            class UpdateCurrentUserProfileDocType : BaseResponse<UserResponse>()
            body(UpdateCurrentUserProfileDocType::class)
        }
    }
}