package io.zinu.migaku.common.utils

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.zinu.migaku.common.exception.AccessTokenInvalidException
import io.zinu.migaku.common.exception.WrongRequestException

//----------------------request extensions----------------------
fun ApplicationCall.userIdOrNull() = principal<UserIdPrincipal>()?.name
fun ApplicationCall.userId() = this.userIdOrNull() ?: throw AccessTokenInvalidException()
fun ApplicationCall.username() = this.param("username")
fun ApplicationCall.conversationId() = this.param("conversationId")
fun ApplicationCall.messageId() = this.param("messageId")
fun ApplicationCall.param(param: String): String {
    return parameters[param] ?: throw WrongRequestException(data = "$param is required")
}

//----------------------application extensions----------------------
inline fun unless(condition: Boolean, block: () -> Unit) {
    if (condition.not()) block()
}