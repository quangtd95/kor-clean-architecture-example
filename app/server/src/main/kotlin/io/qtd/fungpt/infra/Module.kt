package io.qtd.fungpt.infra

import io.ktor.server.application.*
import io.qtd.fungpt.auth.adapter.authModule
import io.qtd.fungpt.common.adapter.commonModule
import io.qtd.fungpt.user.adapter.userModule


fun Application.module() {
    commonModule()
    authModule()
    userModule()
}


