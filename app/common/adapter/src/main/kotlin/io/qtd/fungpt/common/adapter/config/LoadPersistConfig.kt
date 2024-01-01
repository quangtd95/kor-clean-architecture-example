package io.qtd.fungpt.common.adapter.config

import io.ktor.server.config.*


fun loadPersistConfig(hoconConfig: HoconApplicationConfig) = config {
    with(hoconConfig.config("persist.postgres")) {
        postgres {
            driverClassName = property("driverClassName").getString()
            jdbcUrl = property("jdbcUrl").getString()
            maximumPoolSize = property("maximumPoolSize").getString().toInt()
            isAutoCommit = property("isAutoCommit").getString().toBoolean()
            transactionIsolation = property("transactionIsolation").getString()
        }
    }

    persistType {
        hoconConfig.config("persist").property("persistType").getString()
    }


    with(hoconConfig.config("persist.es")) {
        es {
            host = property("host").getString()
            port = property("port").getString().toInt()
            user = property("user").getString()
            password = property("password").getString()
            https = property("https").getString().toBoolean()
        }
    }
}