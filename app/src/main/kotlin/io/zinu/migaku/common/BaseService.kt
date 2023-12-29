package io.zinu.migaku.common

import io.zinu.migaku.modules.database.IDatabaseProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseService : KoinComponent {
    private val dbProvider by inject<io.zinu.migaku.modules.database.IDatabaseProvider>()

    suspend fun <T> dbQuery(block: () -> T): T {
        return dbProvider.dbQuery(block)
    }
}