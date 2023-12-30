package io.zinu.migaku.common.adapter.config

import io.zinu.migaku.common.adapter.database.config.DatabaseConfig
import io.zinu.migaku.common.adapter.database.config.DatabaseConfigBuilder
import io.zinu.migaku.common.adapter.database.config.ESConfig
import io.zinu.migaku.common.adapter.database.config.ESConfigBuilder

data class CommonConfig(
    val databaseConfig: DatabaseConfig,
    val esConfig: ESConfig,
)

class ApplicationConfigBuilder {
    private lateinit var databaseConfig: DatabaseConfig
    private lateinit var esConfig: ESConfig


    fun es(block: ESConfigBuilder.() -> Unit) {
        esConfig = ESConfigBuilder().apply(block).build()
    }

    fun database(block: DatabaseConfigBuilder.() -> Unit) {
        databaseConfig = DatabaseConfigBuilder().apply(block).build()
    }


    fun build(): CommonConfig = CommonConfig(databaseConfig, esConfig)
}

fun config(block: ApplicationConfigBuilder.() -> Unit): CommonConfig =
    ApplicationConfigBuilder().apply(block).build()

