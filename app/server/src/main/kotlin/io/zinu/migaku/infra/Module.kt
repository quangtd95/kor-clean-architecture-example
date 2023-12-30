package io.zinu.migaku.infra

import io.ktor.server.application.*
import io.zinu.migaku.auth.adapter.authModule
import io.zinu.migaku.common.adapter.commonModule
import io.zinu.migaku.user.adapter.userModule


fun Application.module() {
    commonModule()
    authModule()
    userModule()
}


