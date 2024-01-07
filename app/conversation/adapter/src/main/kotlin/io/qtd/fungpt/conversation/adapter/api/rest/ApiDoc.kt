package io.qtd.fungpt.conversation.adapter.api.rest

import io.ktor.http.*
import io.qtd.fungpt.common.adapter.bases.BaseResponse
import io.qtd.fungpt.common.adapter.configs.ApiDoc
import io.qtd.fungpt.common.adapter.configs.SWAGGER_SECURITY_SCHEMA
import io.qtd.fungpt.conversation.adapter.api.dto.ConversationMessageResponse
import io.qtd.fungpt.conversation.adapter.api.dto.ConversationResponse
import io.qtd.fungpt.conversation.adapter.api.dto.PostChat
import kotlinx.coroutines.flow.Flow

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

val deleteSingleConversationDoc: ApiDoc = {
    description = "Delete single conversation"
    request {
        pathParameter<String>("conversationId")
    }
    response {
        HttpStatusCode.OK to {
            class DeleteSingleConversationDocType : BaseResponse<Any>()
            body(DeleteSingleConversationDocType::class)
        }
    }
}

val getSingleConversationsDoc: ApiDoc = {
    description = "Get conversation by id"
    request {
        pathParameter<String>("conversationId")
    }
    response {
        HttpStatusCode.OK to {
            class GetConversationDocType : BaseResponse<ConversationResponse>()
            body(GetConversationDocType::class)
        }
    }
}

val sendNewMessageDoc: ApiDoc = {
    description = "Send new message"
    request {
        pathParameter<String>("conversationId")
        body(PostChat::class)
    }
    response {
        HttpStatusCode.OK to {
            class SendNewMessageDocType : BaseResponse<List<ConversationMessageResponse>>()
            body(SendNewMessageDocType::class)
        }
    }
}

val sendNewMessageStreamDoc: ApiDoc = {
    description = "Send new message stream"
    request {
        pathParameter<String>("conversationId")
        body(PostChat::class)
    }
    response {
        HttpStatusCode.OK to {
            class SendNewMessageStreamDocType : BaseResponse<Flow<String>>()
            body(SendNewMessageStreamDocType::class)
        }
    }
}

val deleteSingleMessageDoc: ApiDoc = {
    description = "Delete single message"
    request {
        pathParameter<String>("conversationId")
        pathParameter<String>("messageId")
    }
    response {
        HttpStatusCode.OK to {
            class DeleteSingleMessageDocType : BaseResponse<Any>()
            body(DeleteSingleMessageDocType::class)
        }
    }
}
