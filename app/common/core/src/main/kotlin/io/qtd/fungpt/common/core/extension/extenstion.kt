package io.qtd.fungpt.common.core.extension

//----------------------application extensions----------------------
inline fun unless(condition: Boolean, block: () -> Unit) {
    if (condition.not()) block()
}

fun randomUUID(): String {
    return java.util.UUID.randomUUID().toString()
}