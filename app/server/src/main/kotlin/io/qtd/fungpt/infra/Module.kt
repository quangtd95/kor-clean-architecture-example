package io.qtd.fungpt.infra

import io.ktor.server.application.*
import io.qtd.fungpt.auth.adapter.authModule
import io.qtd.fungpt.common.adapter.commonModule
import io.qtd.fungpt.profile.adapter.profileModule


fun Application.module() {
    commonModule()
    authModule()
    profileModule()
}


