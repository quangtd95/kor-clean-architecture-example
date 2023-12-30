package io.zinu.migaku.common.core.extension

//----------------------application extensions----------------------
inline fun unless(condition: Boolean, block: () -> Unit) {
    if (condition.not()) block()
}