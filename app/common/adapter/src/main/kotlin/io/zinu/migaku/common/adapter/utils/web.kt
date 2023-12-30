package io.zinu.migaku.common.adapter.utils

import io.ktor.server.application.*
import io.zinu.migaku.common.core.exception.WrongRequestException


fun ApplicationCall.username() = this.param("username")
fun ApplicationCall.conversationId() = this.param("conversationId")
fun ApplicationCall.messageId() = this.param("messageId")
fun ApplicationCall.param(param: String): String {
    return parameters[param] ?: throw WrongRequestException(data = "$param is required")
}
