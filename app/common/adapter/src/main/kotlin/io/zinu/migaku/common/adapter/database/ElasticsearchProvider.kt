package io.zinu.migaku.common.adapter.database

import com.jillesvangurp.ktsearch.KtorRestClient
import com.jillesvangurp.ktsearch.SearchClient
import com.jillesvangurp.ktsearch.createIndex
import com.jillesvangurp.ktsearch.exists
import com.jillesvangurp.searchdsls.mappingdsl.IndexSettingsAndMappingsDSL
import io.zinu.migaku.common.adapter.config.PersistConfig
import io.zinu.migaku.common.core.database.BootPersistStoragePort
import io.zinu.migaku.common.core.database.MustBeCalledInTransactionContext
import io.zinu.migaku.common.core.database.PersistTransactionPort
import io.zinu.migaku.common.core.database.ShutdownPersistStoragePort
import io.zinu.migaku.common.core.extension.unless
import org.slf4j.LoggerFactory

class ElasticsearchProvider(persistConfig: PersistConfig) :
    BootPersistStoragePort,
    ShutdownPersistStoragePort,
    PersistTransactionPort {

    private val esConfig = persistConfig.esConfig
    lateinit var esClient: SearchClient
    private val logger = LoggerFactory.getLogger(ElasticsearchProvider::class.java)

    override suspend fun <T> withNewTransaction(block: suspend () -> T): T {
        return block.invoke()
    }

    @MustBeCalledInTransactionContext
    override suspend fun <T> withExistingTransaction(block: suspend () -> T): T {
        return block.invoke()
    }

    @MustBeCalledInTransactionContext
    override suspend fun <T> withTransaction(block: suspend () -> T): T {
        return block.invoke()
    }

    override suspend fun <T> bootStorage(preInit: suspend () -> T) {
        esClient = SearchClient(
            KtorRestClient(
                host = esConfig.host,
                port = esConfig.port,
                user = esConfig.user,
                password = esConfig.password,
                https = esConfig.https,
            )
        )

        preInit.invoke()
    }

    override fun shutdownStorage() {
        if (this::esClient.isInitialized) {
            esClient.close()
        } else {
            logger.warn("Request to close data source is ignored - it was not open")
        }
    }

    data class IndexCreation(
        val index: String,
        val mappings: IndexSettingsAndMappingsDSL,
    )

    suspend fun createIndexIfNotExists(vararg indexCreation: IndexCreation) {
        indexCreation.forEach {
            unless(esClient.exists(it.index)) {
                esClient.createIndex(it.index, it.mappings)
            }
        }
    }

}