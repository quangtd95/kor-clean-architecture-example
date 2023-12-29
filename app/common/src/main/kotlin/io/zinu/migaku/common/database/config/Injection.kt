package io.zinu.migaku.common.database.config

import io.zinu.migaku.common.database.DatabaseProvider
import io.zinu.migaku.common.database.ESProvider
import io.zinu.migaku.common.database.IDatabaseProvider
import io.zinu.migaku.common.database.IESProvider
import org.koin.dsl.module

val databaseKoinModule = module {
    single<IDatabaseProvider> { DatabaseProvider() }
}

val esKoinModule = module {
    single<IESProvider> { ESProvider() }
}
