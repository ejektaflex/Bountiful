package io.ejekta.bountiful.util

import java.util.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class InlineServ

inline fun <reified T> loadService(): T =
    ServiceLoader.load(T::class.java).findFirst().unwrapService(T::class.java)

fun <T> Optional<T>.unwrapService(type: Class<T>): T =
    orElseThrow { RuntimeException("Could not find Bountiful platform service ${type.simpleName}") }