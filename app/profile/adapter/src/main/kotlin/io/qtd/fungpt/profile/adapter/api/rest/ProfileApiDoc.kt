package io.qtd.fungpt.profile.adapter.api.rest

import io.ktor.http.*
import io.qtd.fungpt.common.adapter.base.BaseResponse
import io.qtd.fungpt.common.adapter.config.ApiDoc
import io.qtd.fungpt.common.adapter.config.SWAGGER_SECURITY_SCHEMA
import io.qtd.fungpt.common.adapter.config.TAG_PROFILE
import io.qtd.fungpt.profile.adapter.api.dto.ProfileResponse

val profilesDoc: ApiDoc = {
    tags = listOf(TAG_PROFILE)
    securitySchemeName = SWAGGER_SECURITY_SCHEMA
}

val getListProfilesDoc: ApiDoc = {
    description = "Get list users"
    response {
        HttpStatusCode.OK to {
            class GetListUsersDocType : BaseResponse<List<ProfileResponse>>()
            body(GetListUsersDocType::class)
        }
    }
}

val getProfileDoc: ApiDoc = {
    description = "Get user by userId"

    request {
        pathParameter<String>("userId")
    }
    response {
        HttpStatusCode.OK to {
            class GetUserDocType : BaseResponse<ProfileResponse>()
            body(GetUserDocType::class)
        }
    }
}

val getCurrentProfileDoc: ApiDoc = {
    description = "Get current user profile"
    response {
        HttpStatusCode.OK to {
            class GetCurrentUserProfileDocType : BaseResponse<ProfileResponse>()
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