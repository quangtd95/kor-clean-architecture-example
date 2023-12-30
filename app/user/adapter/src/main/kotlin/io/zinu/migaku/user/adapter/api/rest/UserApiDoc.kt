package io.zinu.migaku.user.adapter.api.rest

import io.ktor.http.*
import io.zinu.migaku.common.base.BaseResponse
import io.zinu.migaku.common.config.ApiDoc
import io.zinu.migaku.common.config.SWAGGER_SECURITY_SCHEMA
import io.zinu.migaku.common.config.TAG_USER
import io.zinu.migaku.user.adapter.api.dto.UserResponse

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

val getUserDoc: ApiDoc = {
    description = "Get user by userId"

    request {
        pathParameter<String>("userId")
    }
    response {
        HttpStatusCode.OK to {
            class GetUserDocType : BaseResponse<UserResponse>()
            body(GetUserDocType::class)
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

//val updateCurrentUserProfileDoc: ApiDoc = {
//    description = "Update current user profile"
//    request {
//        body(UpdateUserRequest::class)
//    }
//    response {
//        HttpStatusCode.OK to {
//            class UpdateCurrentUserProfileDocType : BaseResponse<UserResponse>()
//            body(UpdateCurrentUserProfileDocType::class)
//        }
//    }
//}