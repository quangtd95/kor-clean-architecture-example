package io.zinu.migaku.auth.adapter.persist.postgres.repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.zinu.migaku.auth.adapter.persist.postgres.entity.RefreshTokens
import io.zinu.migaku.auth.adapter.persist.postgres.entity.Users
import io.zinu.migaku.auth.core.repository.BootPersistStoragePort
import io.zinu.migaku.auth.core.repository.MustBeCalledInTransactionContext
import io.zinu.migaku.auth.core.repository.PersistTransactionPort
import io.zinu.migaku.auth.core.repository.ShutdownPersistStoragePort
import io.zinu.migaku.common.config.CommonConfig
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.withSuspendTransaction
import org.jetbrains.exposed.sql.transactions.transactionManager
import org.slf4j.LoggerFactory

internal class PostgresDatabaseProvider(
    private val commonConfig: CommonConfig
) :
    BootPersistStoragePort,
    ShutdownPersistStoragePort,
    PersistTransactionPort {
    private val logger = LoggerFactory.getLogger(PostgresDatabaseProvider::class.java)
    private lateinit var ds: HikariDataSource
    private lateinit var db: Database

    private val tables = arrayOf(
        Users,
        RefreshTokens,
    )

    override suspend fun <T> bootStorage(preInit: suspend () -> T) {
        logger.info("Initializing database...")
        ds = hikari()
        db = Database.connect(ds)

        withNewTransaction {
            preInit.invoke()
            SchemaUtils.create(*tables)
        }

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

    override fun shutdownStorage() {
        if (this::ds.isInitialized) {
            ds.close()
        } else {
            logger.warn("Request to close data source is ignored - it was not open")
        }
    }

    override suspend fun <T> withNewTransaction(block: suspend () -> T): T {
        return try {
            newSuspendedTransaction(Dispatchers.IO, db = db) {
                block()
            }
        } catch (e: ExposedSQLException) {
            logger.error("withNewTransaction(): SQl error while executing transaction")
            throw e
        }
    }

    @MustBeCalledInTransactionContext
    override suspend fun <T> withExistingTransaction(block: suspend () -> T): T {
        val tx = db.transactionManager.currentOrNull()
        if (tx == null) {
            throw IllegalStateException("withExistingTransaction(): no current transaction in context")
        } else if (tx.connection.isClosed) {
            throw IllegalStateException("withExistingTransaction(): current transaction is closed")
        }
        return try {
            tx.withSuspendTransaction {
                block()
            }
        } catch (e: ExposedSQLException) {
            logger.error("withExistingTransaction(): SQl error while executing existing transaction")
            throw e
        }
    }

    @MustBeCalledInTransactionContext
    override suspend fun <T> withTransaction(block: suspend () -> T): T {
        val tx = TransactionManager.currentOrNull()
        return try {
            if (tx == null || tx.connection.isClosed) {
                newSuspendedTransaction(Dispatchers.IO, db = db) {
                    block()
                }
            } else {
                tx.withSuspendTransaction {
                    block()
                }
            }
        } catch (e: ExposedSQLException) {
            logger.error("transaction(): SQl error while executing transaction", e)
            throw e
        }
    }

}