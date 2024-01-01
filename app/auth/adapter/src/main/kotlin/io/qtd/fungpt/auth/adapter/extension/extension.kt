package io.qtd.fungpt.auth.adapter.extension

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.qtd.fungpt.common.core.exception.AccessTokenInvalidException

//----------------------request extensions----------------------
fun ApplicationCall.userIdOrNull() = principal<UserIdPrincipal>()?.name
fun ApplicationCall.userId() = this.userIdOrNull() ?: throw AccessTokenInvalidException()