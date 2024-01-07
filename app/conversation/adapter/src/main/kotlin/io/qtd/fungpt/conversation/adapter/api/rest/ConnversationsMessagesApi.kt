package io.qtd.fungpt.conversation.adapter.api.rest

import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.qtd.fungpt.common.adapter.bases.BaseResponse
import io.qtd.fungpt.common.adapter.bases.baseRespond
import io.qtd.fungpt.common.adapter.utils.Constants.JWT_AUTH
import io.qtd.fungpt.common.adapter.utils.conversationId
import io.qtd.fungpt.common.adapter.utils.messageId
import io.qtd.fungpt.common.adapter.utils.userId
import io.qtd.fungpt.conversation.adapter.api.dto.PostChat
import io.qtd.fungpt.conversation.adapter.api.dto.toApiResponse
import io.qtd.fungpt.conversation.core.usecases.ConversationMessageUsecase
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.koin.ktor.ext.inject

fun Route.conversationsMessages() {
    val conversationMessageUsecase: ConversationMessageUsecase by inject()

    route("/conversations/{conversationId}/messages", conversationsDocs) {
        authenticate(JWT_AUTH) {
            get {
                val messages = conversationMessageUsecase.getMessages(call.userId(), call.conversationId())
                    .map { it.toApiResponse() }
                    .toList()
                call.baseRespond(BaseResponse.success(messages))
            }

            post(sendNewMessageDoc) {
                val message = call.receive<PostChat>()
                val response = conversationMessageUsecase.postMessage(
                    call.userId(), call.conversationId(), message.content
                )
                    .map { it.toApiResponse() }
                    .toList()
                call.respond(BaseResponse.success(response))
            }

            post("/stream", sendNewMessageStreamDoc) {
                val message = call.receive<PostChat>()
                val response = conversationMessageUsecase.postMessageStream(
                    call.userId(), call.conversationId(), message.content
                )
                call.respondSse(response)
            }

            delete("/{messageId}", deleteSingleMessageDoc) {
                conversationMessageUsecase.deleteMessage(
                    call.userId(), call.conversationId(), call.messageId()
                )
                call.baseRespond(BaseResponse.success())
            }
        }
    }
}