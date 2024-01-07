package io.qtd.fungpt.profile.adapter.api.rest

import io.ktor.http.*
import io.qtd.fungpt.common.adapter.bases.BaseResponse
import io.qtd.fungpt.common.adapter.configs.ApiDoc
import io.qtd.fungpt.common.adapter.configs.SWAGGER_SECURITY_SCHEMA
import io.qtd.fungpt.profile.adapter.api.dto.ProfileResponse

val profilesDoc: ApiDoc = {
    tags = listOf("Profile")
    securitySchemeName = SWAGGER_SECURITY_SCHEMA
}

val getListProfilesDoc: ApiDoc = {
    description = "Get list profiles"
    response {
        HttpStatusCode.OK to {
            class GetListProfilesDocType : BaseResponse<List<ProfileResponse>>()
            body(GetListProfilesDocType::class)
        }
    }
}

val getProfileDoc: ApiDoc = {
    description = "Get profile by profileId"

    request {
        pathParameter<String>("profileId")
    }
    response {
        HttpStatusCode.OK to {
            class GetProfileDocType : BaseResponse<ProfileResponse>()
            body(GetProfileDocType::class)
        }
    }
}

val getCurrentProfileDoc: ApiDoc = {
    description = "Get current profile profile"
    response {
        HttpStatusCode.OK to {
            class GetCurrentProfileProfileDocType : BaseResponse<ProfileResponse>()
            body(GetCurrentProfileProfileDocType::class)
        }
    }
}

//val updateCurrentProfileProfileDoc: ApiDoc = {
//    description = "Update current profile profile"
//    request {
//        body(UpdateProfileRequest::class)
//    }
//    response {
//        HttpStatusCode.OK to {
//            class UpdateCurrentProfileProfileDocType : BaseResponse<ProfileResponse>()
//            body(UpdateCurrentProfileProfileDocType::class)
//        }
//    }
//}