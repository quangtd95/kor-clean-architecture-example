package io.zinu.migaku.modules

import io.zinu.migaku.modules.auth.api.auth
import io.zinu.migaku.modules.profile.api.user
import io.ktor.server.routing.*

fun Routing.api() {
    route("/api") {
        auth()
        user()
    }
}