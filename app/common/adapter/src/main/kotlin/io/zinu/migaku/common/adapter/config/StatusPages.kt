package io.zinu.migaku.common.adapter.config

import com.aallam.openai.api.exception.PermissionException
import io.zinu.migaku.common.adapter.base.BaseResponse.Companion.authenticationError
import io.zinu.migaku.common.adapter.base.BaseResponse.Companion.permissionError
import io.zinu.migaku.common.adapter.base.BaseResponse.Companion.serverError
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.util.*
import io.zinu.migaku.common.adapter.base.BaseResponse.Companion.badRequestError
import io.zinu.migaku.common.adapter.base.baseRespond
import io.zinu.migaku.common.core.exception.AuthenticationException
import io.zinu.migaku.common.core.exception.WrongRequestException
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