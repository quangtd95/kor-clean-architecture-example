package io.qtd.fungpt.common.adapter.utils

import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.util.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("DataTransformationBenchmarkPlugin")
val DataTransformationBenchmarkPlugin = createApplicationPlugin(name = "DataTransformationBenchmarkPlugin") {
    val onCallTimeKey = AttributeKey<Long>("onCallTimeKey")
    onCall { call ->
        val onCallTime = System.currentTimeMillis()
        call.attributes.put(onCallTimeKey, onCallTime)
    }

    onCallReceive { call ->
        val onCallTime = call.attributes[onCallTimeKey]
        val onCallReceiveTime = System.currentTimeMillis()
        logger.info("onCallReceive in (ms): ${onCallReceiveTime - onCallTime}")
    }

    onCallRespond { call ->
        val onCallTime = call.attributes[onCallTimeKey]
        val onCallReceiveTime = System.currentTimeMillis()
        logger.info("onCallRespond in (ms): ${onCallReceiveTime - onCallTime}")
    }

    on(ResponseSent) { call ->
        val onCallTime = call.attributes[onCallTimeKey]
        val onCallReceiveTime = System.currentTimeMillis()
        logger.info("ResponseSent in (ms): ${onCallReceiveTime - onCallTime}")
    }

}