package io.qtd.fungpt.common.adapter.configs

import com.aallam.openai.api.exception.PermissionException
import io.qtd.fungpt.common.adapter.bases.BaseResponse.Companion.authenticationError
import io.qtd.fungpt.common.adapter.bases.BaseResponse.Companion.permissionError
import io.qtd.fungpt.common.adapter.bases.BaseResponse.Companion.serverError
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.util.*
import io.qtd.fungpt.common.adapter.bases.BaseResponse.Companion.badRequestError
import io.qtd.fungpt.common.adapter.bases.baseRespond
import io.qtd.fungpt.common.core.exception.AuthenticationException
import io.qtd.fungpt.common.core.exception.WrongRequestException
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