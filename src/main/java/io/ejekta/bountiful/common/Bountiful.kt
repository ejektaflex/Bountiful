@file:UseSerializers(IdentitySer::class)
package io.ejekta.bountiful.common

import io.ejekta.bountiful.common.config.BountifulIO
import io.ejekta.kambrikx.serializers.IdentitySer
import kotlinx.serialization.UseSerializers
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier

class Bountiful : ModInitializer {

    companion object {
        const val ID = "bountiful"
        fun id(str: String) = Identifier(ID, str)
    }

    init {
        ResourceManagerHelper
            .get(ResourceType.SERVER_DATA)
            .registerReloadListener(BountifulIO)
    }

    override fun onInitialize() {
        println("Common init")
        BountifulIO.loadConfig()
    }

}