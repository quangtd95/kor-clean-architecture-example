package io.qtd.fungpt.common.adapter.utils

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.qtd.fungpt.common.core.exception.AccessTokenInvalidException
import io.qtd.fungpt.common.core.exception.WrongRequestException


fun ApplicationCall.username() = this.param("username")
fun ApplicationCall.conversationId() = this.param("conversationId")
fun ApplicationCall.messageId() = this.param("messageId")
fun ApplicationCall.param(param: String): String {
    return parameters[param] ?: throw WrongRequestException(data = "$param is required")
}
//----------------------request extensions----------------------
fun ApplicationCall.userIdOrNull() = principal<UserIdPrincipal>()?.name
fun ApplicationCall.userId() = this.userIdOrNull() ?: throw AccessTokenInvalidException()