package io.ejekta.kambrik.internal.registration

import io.ejekta.kambrik.ext.register
import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.registration.KambricAutoRegistrar
import net.fabricmc.loader.api.entrypoint.EntrypointContainer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

internal object KambrikRegistrar {

    data class RegistrationEntry<T>(val registry: Registry<T>, val itemId: String, val item: T) {
        fun register(modId: String) = registry.register(Identifier(modId, itemId), item)
    }

    data class ModResistrar(val requestor: KambricAutoRegistrar, val content: MutableList<RegistrationEntry<*>> = mutableListOf())

    private val registrars = mutableMapOf<KambricAutoRegistrar, ModResistrar>()

    operator fun get(requester: KambricAutoRegistrar): ModResistrar {
        return registrars.getOrPut(requester) { ModResistrar(requester) }
    }

    fun <T> register(requester: KambricAutoRegistrar, reg: Registry<T>, itemId: String, obj: T): T {
        println("Kambrik registering '${requester::class.qualifiedName} for $itemId' for autoregistration")
        this[requester].content.add(RegistrationEntry(reg, itemId, obj))
        return obj
    }

    fun doRegistrationFor(container: EntrypointContainer<KambrikMarker>) {
        println("Kambrik doing real registration for mod ${container.provider.metadata.id}")
        val registrar = this[container.entrypoint as? KambricAutoRegistrar ?: return]
        registrar.requestor.manualRegister()
        registrar.content.forEach { entry ->
            entry.register(container.provider.metadata.id)
        }
    }

}