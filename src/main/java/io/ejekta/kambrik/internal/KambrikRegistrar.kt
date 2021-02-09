package io.ejekta.kambrik.internal

import io.ejekta.kambrik.ext.register
import net.minecraft.util.Identifier
import net.minecraft.util.registry.SimpleRegistry

object KambrikRegistrar {
    //data class RegistryMap<T>(val registry: SimpleRegistry<T>, val items: MutableMap<String, T>)
    //data class ModRegistrar(val modId: String, val content: MutableList<RegistryMap<*>>)

    data class RegistrationEntry<T>(val registry: SimpleRegistry<T>, val id: Identifier, val item: T) {
        fun register() = registry.register(id, item)
    }
    data class ModResistrar(val modId: String, val content: MutableList<RegistrationEntry<*>> = mutableListOf())

    val registrars = mutableMapOf<String, ModResistrar>()

    operator fun get(modId: String): ModResistrar {
        return registrars.getOrPut(modId) { ModResistrar(modId) }
    }

    fun <T> register(modId: String, reg: SimpleRegistry<T>, itemId: String, obj: T): T {
        println("Kambrik registering '$modId:$itemId' for autoregistration")
        this[modId].content.add(RegistrationEntry(reg, Identifier(modId, itemId), obj))
        return obj
    }

    fun doRegistrationFor(modId: String) {
        println("Kambrik doing real registration for mod $modId")
        this[modId].content.forEach { entry -> entry.register() }
    }

}