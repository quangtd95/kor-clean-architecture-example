package io.qtd.fungpt.common.core.bases

import org.koin.core.module.Module

//base class contains all above functions to be called in main module
abstract class CoreModuleCreation {
    abstract fun setupKoinModule(): Module
}
