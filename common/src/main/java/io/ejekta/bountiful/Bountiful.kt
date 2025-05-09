package io.ejekta.bountiful

import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.kambrik.Kambrik
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation

class Bountiful {
    companion object {
        const val ID = "bountiful"
        const val nightly = true
        var packMode = false
        fun id(str: String) = ResourceLocation.fromNamespaceAndPath(ID, str)
        val LOGGER = Kambrik.Logging.createLogger(ID)
        val BOUNTY_LOGIC_REGISTRY_KEY: ResourceKey<Registry<IBountyType>> = ResourceKey.createRegistryKey(id("logic_registry"))
    }
}