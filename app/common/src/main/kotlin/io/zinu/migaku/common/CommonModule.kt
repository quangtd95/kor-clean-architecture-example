package io.zinu.migaku.common

import io.zinu.migaku.common.database.*
import org.koin.dsl.binds
import org.koin.dsl.module

val commonKoinModule = module {
    single { PostgresDatabaseProvider(get()) } binds arrayOf(
        BootPersistStoragePort::class,
        ShutdownPersistStoragePort::class,
        PersistTransactionPort::class
    )
    single<IESProvider> { ESProvider() }
}
