package io.zinu.migaku.modules.profile.api

import io.zinu.migaku.config.ApiDoc
import io.zinu.migaku.config.SWAGGER_SECURITY_SCHEMA
import io.zinu.migaku.config.TAG_PROFILE
import io.zinu.migaku.common.BaseResponse
import io.zinu.migaku.modules.profile.model.ProfileResponse
import io.ktor.http.*

val profilesDoc: ApiDoc = {
    tags = listOf(TAG_PROFILE)
}

val getProfileDoc: ApiDoc = {
    description = "Get profile of user"
    request {
        pathParameter<String>("username")
    }
    response {
        HttpStatusCode.OK to {
            class GetProfileResponseType : BaseResponse<ProfileResponse>()
            body(GetProfileResponseType::class)
        }
    }
}

val followDoc : ApiDoc = {
    securitySchemeName = SWAGGER_SECURITY_SCHEMA
}

val postFollowProfileDoc: ApiDoc = {
    description = "Follow profile of user"
    request {
        pathParameter<String>("username")
    }
    response {
        HttpStatusCode.OK to {
            class PostFollowProfileResponseType : BaseResponse<ProfileResponse>()
            body(PostFollowProfileResponseType::class)
        }
    }
}

val deleteFollowProfileDoc : ApiDoc = {
    description = "Unfollow profile of user"
    request {
        pathParameter<String>("username")
    }
    response {
        HttpStatusCode.OK to {
            class DeleteFollowProfileResponseType : BaseResponse<ProfileResponse>()
            body(DeleteFollowProfileResponseType::class)
        }
    }
}