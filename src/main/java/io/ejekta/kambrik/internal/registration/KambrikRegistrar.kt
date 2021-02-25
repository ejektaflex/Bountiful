package io.ejekta.kambrik.internal.registration

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.register
import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.feature.registration.KambrikAutoRegistrar
import net.fabricmc.loader.api.entrypoint.EntrypointContainer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

internal object KambrikRegistrar {

    data class RegistrationEntry<T>(val registry: Registry<T>, val itemId: String, val item: T) {
        fun register(modId: String) = registry.register(Identifier(modId, itemId), item)
    }

    data class ModResistrar(val requestor: KambrikAutoRegistrar, val content: MutableList<RegistrationEntry<*>> = mutableListOf())

    private val registrars = mutableMapOf<KambrikAutoRegistrar, ModResistrar>()

    operator fun get(requester: KambrikAutoRegistrar): ModResistrar {
        return registrars.getOrPut(requester) { ModResistrar(requester) }
    }

    fun <T> register(requester: KambrikAutoRegistrar, reg: Registry<T>, itemId: String, obj: T): T {
        Kambrik.Logger.debug("Kambrik registering '${requester::class.qualifiedName} for $itemId' for autoregistration")
        this[requester].content.add(RegistrationEntry(reg, itemId, obj))
        return obj
    }

    fun doRegistrationFor(container: EntrypointContainer<KambrikMarker>) {
        Kambrik.Logger.debug("Kambrik doing real registration for mod ${container.provider.metadata.id}")
        this[container.entrypoint as? KambrikAutoRegistrar ?: return].apply {
            requestor.manualRegister()
            content.forEach { entry ->
                entry.register(container.provider.metadata.id)
            }
        }
    }

}