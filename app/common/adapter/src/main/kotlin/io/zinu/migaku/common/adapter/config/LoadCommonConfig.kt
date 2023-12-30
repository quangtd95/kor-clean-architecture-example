package io.zinu.migaku.common.adapter.config

import io.ktor.server.config.*


fun loadCommonConfig(hoconConfig: HoconApplicationConfig) = config {
    database {
        with(hoconConfig.config("database")) {
            driverClassName = property("driverClassName").getString()
            jdbcUrl = property("jdbcUrl").getString()
            maximumPoolSize = property("maximumPoolSize").getString().toInt()
            isAutoCommit = property("isAutoCommit").getString().toBoolean()
            transactionIsolation = property("transactionIsolation").getString()
        }
    }

    es {
        with(hoconConfig.config("es")) {
            host = property("host").getString()
            port = property("port").getString().toInt()
            user = property("user").getString()
            password = property("password").getString()
            https = property("https").getString().toBoolean()
        }
    }
}