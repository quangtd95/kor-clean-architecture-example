package io.zinu.migaku.config

import io.zinu.migaku.exception.AuthenticationException
import io.zinu.migaku.common.BaseResponse.Companion.badRequestError
import io.zinu.migaku.exception.WrongRequestException
import io.zinu.migaku.exception.PermissionException
import io.zinu.migaku.common.BaseResponse.Companion.authenticationError
import io.zinu.migaku.common.BaseResponse.Companion.permissionError
import io.zinu.migaku.common.BaseResponse.Companion.serverError
import io.zinu.migaku.common.baseRespond
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.util.*
import org.slf4j.LoggerFactory

@OptIn(InternalAPI::class)
fun StatusPagesConfig.statusPages() {
    val logger = LoggerFactory.getLogger(StatusPagesConfig::class.java)
    exception<Throwable> { call, cause ->
        when (cause) {
            is WrongRequestException -> call.baseRespond(
                badRequestError(
                    cause.message ?: "Bad request error",
                    cause.data
                )
            )

            is BadRequestException -> {
                logger.trace(cause.stackTraceToString())
                call.baseRespond(badRequestError(cause.rootCause?.message ?: cause.message ?: "Bad request error"))
            }

            is PermissionException -> call.baseRespond(permissionError(cause.message ?: "Permission denied"))
            is AuthenticationException -> call.baseRespond(authenticationError(cause.message ?: "Authentication error"))
            else -> {
                logger.trace(cause.stackTraceToString())
                call.baseRespond(serverError(cause.message ?: "Server error"))
            }
        }
    }
}