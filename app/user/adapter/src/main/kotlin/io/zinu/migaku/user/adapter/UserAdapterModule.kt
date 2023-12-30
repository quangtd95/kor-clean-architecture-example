package io.zinu.migaku.user.adapter

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.zinu.migaku.user.adapter.api.rest.user
import org.koin.dsl.module

val userAdapterKoinModule = module {

}

fun Application.userModule() {
    routing {
        route("/api") {
            user()
        }
    }
}