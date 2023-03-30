@file:UseSerializers(IdentitySer::class)
package io.ejekta.bountiful

import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.serial.serializers.IdentitySer
import kotlinx.serialization.UseSerializers
import net.fabricmc.api.ModInitializer
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier


class Bountiful {

    companion object {
        const val ID = "bountiful"
        fun id(str: String) = Identifier(ID, str)
        val LOGGER = Kambrik.Logging.createLogger(ID)
        val BOUNTY_LOGIC_REGISTRY_KEY: RegistryKey<Registry<IBountyType>> = RegistryKey.ofRegistry(id("logic_registry"))
    }

}