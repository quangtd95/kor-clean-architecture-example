package io.zinu.migaku.modules.database.config

import io.zinu.migaku.modules.database.DatabaseProvider
import io.zinu.migaku.modules.database.ESProvider
import io.zinu.migaku.modules.database.IDatabaseProvider
import io.zinu.migaku.modules.database.IESProvider
import org.koin.dsl.module

val databaseKoinModule = module {
    single<io.zinu.migaku.modules.database.IDatabaseProvider> { io.zinu.migaku.modules.database.DatabaseProvider() }
}

val esKoinModule = module {
    single<IESProvider> { ESProvider() }
}
