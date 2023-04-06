package io.ejekta.bountiful

import io.ejekta.bountiful.bridge.BountifulSharedApi
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModList
import net.minecraftforge.registries.RegisterEvent

class BountifulForgeApi : BountifulSharedApi {

    override fun isModLoaded(id: String): Boolean {
        return ModList.get().isLoaded(id)
    }

    @SubscribeEvent
    fun registerRegistryContent(evt: RegisterEvent) {
        BountifulContent.getId()
        KambrikRegistrar[BountifulContent].content.forEach { entry ->
            @Suppress("UNCHECKED_CAST")
            evt.register(entry.registry.key as RegistryKey<out Registry<Any>>, Identifier(BountifulContent.getId(), entry.itemId)) { entry.item }
        }
    }

}