package io.qtd.fungpt.common.adapter.bases

import io.ktor.server.application.*
import org.koin.core.module.Module

//base class contains all above functions to be called in main module
abstract class AdapterModuleCreation {
    abstract suspend fun preInitDatabase()
    abstract fun setupRoutingAndPlugin(app: Application)
    abstract fun setupKoinModule(): Module
}
