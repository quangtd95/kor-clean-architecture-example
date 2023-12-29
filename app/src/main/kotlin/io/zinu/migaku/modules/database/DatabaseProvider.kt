package io.zinu.migaku.modules.database

import io.zinu.migaku.config.ApplicationConfig
import io.zinu.migaku.modules.auth.model.Followings
import io.zinu.migaku.modules.auth.model.RefreshTokens
import io.zinu.migaku.modules.auth.model.Users
import io.zinu.migaku.modules.conversation.model.ConversationMessages
import io.zinu.migaku.modules.conversation.model.Conversations
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


interface IDatabaseProvider {
    fun init()
    suspend fun <T> dbQuery(block: () -> T): T
    suspend fun drop()
}

@OptIn(DelicateCoroutinesApi::class)
class DatabaseProvider : io.zinu.migaku.modules.database.IDatabaseProvider, KoinComponent {
    private val applicationConfig by inject<ApplicationConfig>()
    private val dispatcher: CoroutineDispatcher = newFixedThreadPoolContext(5, "database-pool")

    override fun init() {
        Database.connect(hikari())
        transaction {
            create(Users, Followings, RefreshTokens, Conversations, ConversationMessages)
        }
    }

    private fun hikari(): HikariDataSource {
        val dbConfig = applicationConfig.databaseConfig
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

    override suspend fun drop() {
        dbQuery { drop(Users, Followings, RefreshTokens, Conversations, ConversationMessages) }
    }

}