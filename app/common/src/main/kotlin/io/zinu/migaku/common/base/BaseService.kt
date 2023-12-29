package io.zinu.migaku.common.base

import io.zinu.migaku.common.database.IDatabaseProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseService : KoinComponent {
    private val dbProvider by inject<IDatabaseProvider>()

    suspend fun <T> dbQuery(block: () -> T): T {
        return dbProvider.dbQuery(block)
    }
}