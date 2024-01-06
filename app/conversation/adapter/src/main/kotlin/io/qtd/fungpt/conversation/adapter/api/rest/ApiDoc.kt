package io.qtd.fungpt.conversation.adapter.api.rest

import io.ktor.http.*
import io.qtd.fungpt.common.adapter.bases.BaseResponse
import io.qtd.fungpt.common.adapter.configs.ApiDoc
import io.qtd.fungpt.common.adapter.configs.SWAGGER_SECURITY_SCHEMA
import io.qtd.fungpt.conversation.adapter.api.dto.ConversationResponse

val conversationsDocs: ApiDoc = {
    tags = listOf("Conversation")
    securitySchemeName = SWAGGER_SECURITY_SCHEMA
}

val createConversationDoc: ApiDoc = {
    description = "Create conversation"
    response {
        HttpStatusCode.Created to {
            class CreateConversationDocType : BaseResponse<ConversationResponse>()
            body(CreateConversationDocType::class)
        }
    }
}

val getListConversationsDoc: ApiDoc = {
    description = "Get conversations"
    response {
        HttpStatusCode.OK to {
            class GetListConversationsDocType : BaseResponse<List<ConversationResponse>>()
            body(GetListConversationsDocType::class)
        }
    }
}

val deleteAllConversationsDoc: ApiDoc = {
    description = "Delete conversations"
    response {
        HttpStatusCode.OK to {
            class DeleteAllConversationsDocType : BaseResponse<Any>()
            body(DeleteAllConversationsDocType::class)
        }
    }
}

