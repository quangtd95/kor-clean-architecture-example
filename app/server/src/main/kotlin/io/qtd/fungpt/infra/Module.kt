package io.qtd.fungpt.infra

import io.ktor.server.application.*
import io.qtd.fungpt.common.adapter.bases.AdapterModuleCreation


fun Application.module(adapterEntries: List<AdapterModuleCreation>) {
    adapterEntries.forEach {
        it.setupRoutingAndPlugin(this)
    }
}


