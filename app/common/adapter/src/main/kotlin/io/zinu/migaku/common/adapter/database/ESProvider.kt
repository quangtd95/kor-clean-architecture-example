package io.zinu.migaku.common.adapter.database

import com.jillesvangurp.ktsearch.KtorRestClient
import com.jillesvangurp.ktsearch.SearchClient
import com.jillesvangurp.ktsearch.createIndex
import com.jillesvangurp.ktsearch.exists
import io.zinu.migaku.common.adapter.config.CommonConfig
import io.zinu.migaku.common.core.extension.unless
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface IESProvider {
    fun init()
    suspend fun <T> dbQuery(block: () -> T): T
    suspend fun drop()
}

class ESProvider : IESProvider, KoinComponent {

    private val commonConfig by inject<CommonConfig>()
    private val esConfig = commonConfig.esConfig
    private val client = SearchClient(
        KtorRestClient(
            host = esConfig.host,
            port = esConfig.port,
            user = esConfig.user,
            password = esConfig.password,
            https = esConfig.https,
        )
    )

    override fun init() {
        runBlocking(Dispatchers.IO) {
            unless(client.exists("ktor")) {
                client.createIndex("ktor")
            }
        }
    }


    override suspend fun <T> dbQuery(block: () -> T): T {
        TODO()
    }

    override suspend fun drop() {
        TODO()
    }
}