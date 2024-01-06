package io.qtd.fungpt.conversation.adapter.api.rest

import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import io.ktor.websocket.*
import io.qtd.fungpt.common.adapter.bases.BaseResponse.Companion.created
import io.qtd.fungpt.common.adapter.bases.BaseResponse.Companion.success
import io.qtd.fungpt.common.adapter.bases.baseRespond
import io.qtd.fungpt.common.adapter.utils.Constants.JWT_AUTH
import io.qtd.fungpt.common.adapter.utils.userId
import io.qtd.fungpt.conversation.adapter.api.*
import io.qtd.fungpt.conversation.adapter.api.dto.toApiResponse
import io.qtd.fungpt.conversation.core.models.CoreConversation
import io.qtd.fungpt.conversation.core.usecases.ConversationUsecase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.toList
import org.koin.ktor.ext.inject

fun Route.conversations() {
    val conversationUsecase: ConversationUsecase by inject()

    route("/conversations", conversationsDocs) {
        authenticate(JWT_AUTH) {
            post(createConversationDoc) {
                val conversation = conversationUsecase.createConversation(call.userId())
                    .toApiResponse()
                call.baseRespond(created(conversation))
            }

            get(getListConversationsDoc) {
                val conversationList = conversationUsecase.getConversations(call.userId())
                    .map(CoreConversation::toApiResponse)
                    .toList()
                call.baseRespond(success(conversationList))
            }
        }
    }
}

/**
 * Method that responds an [ApplicationCall] by reading all the [SseEvent]s from the specified [eventFlow] [Flow]
 * and serializing them in a way that is compatible with the Server-Sent Events specification.
 *
 * You can read more about it here: https://www.html5rocks.com/en/tutorials/eventsource/basics/
 */
suspend fun ApplicationCall.respondSse(eventFlow: Flow<String>) {
    response.cacheControl(CacheControl.NoCache(null))
    respondBytesWriter(contentType = ContentType.Text.EventStream) {
        eventFlow.onCompletion {
            flush()
            close()
        }.collect { event ->
            for (dataLine in event.lines()) {
                writeStringUtf8("data: $dataLine\n")
            }
            writeStringUtf8("\n")
            flush()
        }
    }
}
