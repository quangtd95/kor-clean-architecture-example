package io.qtd.fungpt.common.adapter.databases

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.qtd.fungpt.common.adapter.databases.config.PersistConfig
import io.qtd.fungpt.common.core.database.BootPersistStoragePort
import io.qtd.fungpt.common.core.database.MustBeCalledInTransactionContext
import io.qtd.fungpt.common.core.database.PersistTransactionPort
import io.qtd.fungpt.common.core.database.ShutdownPersistStoragePort
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.withSuspendTransaction
import org.jetbrains.exposed.sql.transactions.transactionManager
import org.slf4j.LoggerFactory

class PostgresProvider(
    private val persistConfig: PersistConfig
) :
    BootPersistStoragePort,
    ShutdownPersistStoragePort,
    PersistTransactionPort {
    private val logger = LoggerFactory.getLogger(PostgresProvider::class.java)
    private lateinit var ds: HikariDataSource
    private lateinit var db: Database

    override suspend fun <T> bootStorage(preInit: suspend () -> T) {
        logger.info("Initializing database...")
        ds = hikari()
        db = Database.connect(ds)

        withNewTransaction {
            preInit.invoke()
        }

    }

    private fun hikari(): HikariDataSource {
        val dbConfig = persistConfig.postgresConfig
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