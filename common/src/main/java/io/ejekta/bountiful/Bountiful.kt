package io.ejekta.bountiful

import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.kambrik.Kambrik
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.resources.ResourceLocation

class Bountiful {
    companion object {
        const val ID = "bountiful"
        const val nightly = false
        fun id(str: String) = ResourceLocation.parse(ID, str)
        val LOGGER = Kambrik.Logging.createLogger(ID)
        val BOUNTY_LOGIC_REGISTRY_KEY: RegistryKey<Registry<IBountyType>> = RegistryKey.ofRegistry(id("logic_registry"))
    }
}