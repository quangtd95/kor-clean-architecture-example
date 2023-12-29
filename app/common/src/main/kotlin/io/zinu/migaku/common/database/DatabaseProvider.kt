package io.zinu.migaku.common.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.zinu.migaku.common.config.CommonConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


interface IDatabaseProvider {
    fun init(block: () -> Unit)
    suspend fun <T> dbQuery(block: () -> T): T
}

@OptIn(DelicateCoroutinesApi::class)
class DatabaseProvider : IDatabaseProvider, KoinComponent {
    private val commonConfig by inject<CommonConfig>()
    private val dispatcher: CoroutineDispatcher = newFixedThreadPoolContext(5, "database-pool")

    override fun init(block: () -> Unit) {
        Database.connect(hikari())
        block()
    }

    private fun hikari(): HikariDataSource {
        val dbConfig = commonConfig.databaseConfig
        HikariConfig().run {
            driverClassName = dbConfig.driverClassName
            jdbcUrl = dbConfig.jdbcUrl
            username = dbConfig.username
            password = dbConfig.password
            maximumPoolSize = dbConfig.maximumPoolSize
            isAutoCommit = dbConfig.isAutoCommit
            transactionIsolation = dbConfig.transactionIsolation
            return HikariDataSource(this)
        }
    }

    override suspend fun <T> dbQuery(block: () -> T): T = withContext(dispatcher) {
        transaction { block() }
    }
}