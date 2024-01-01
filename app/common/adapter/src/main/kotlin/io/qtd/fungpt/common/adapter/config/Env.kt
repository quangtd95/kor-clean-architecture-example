package io.qtd.fungpt.common.adapter.config

import io.qtd.fungpt.common.adapter.database.config.PostgresConfig
import io.qtd.fungpt.common.adapter.database.config.DatabaseConfigBuilder
import io.qtd.fungpt.common.adapter.database.config.ESConfig
import io.qtd.fungpt.common.adapter.database.config.ESConfigBuilder
import java.util.*

enum class PersistType {
    POSTGRES,
    ES
}

data class PersistConfig(
    val postgresConfig: PostgresConfig,
    val esConfig: ESConfig,
    val persistType: PersistType = PersistType.POSTGRES
)

class ApplicationConfigBuilder {
    private lateinit var postgresConfig: PostgresConfig
    private lateinit var esConfig: ESConfig
    private lateinit var persistType: PersistType


    fun es(block: ESConfigBuilder.() -> Unit) {
        esConfig = ESConfigBuilder().apply(block).build()
    }

    fun postgres(block: DatabaseConfigBuilder.() -> Unit) {
        postgresConfig = DatabaseConfigBuilder().apply(block).build()
    }

    fun persistType(block: () -> String) {
        this.persistType = PersistType.valueOf(block().uppercase(Locale.getDefault()))
    }

    fun build(): PersistConfig = PersistConfig(postgresConfig, esConfig, persistType)
}

fun config(block: ApplicationConfigBuilder.() -> Unit): PersistConfig =
    ApplicationConfigBuilder().apply(block).build()

