@file:UseSerializers(IdentitySer::class)
package io.ejekta.bountiful.common

import io.ejekta.bountiful.common.config.BountifulIO
import io.ejekta.bountiful.common.content.BountifulContent
import io.ejekta.bountiful.common.serial.IdentitySer
import kotlinx.serialization.ExperimentalSerializationApi
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

    @ExperimentalSerializationApi
    @ExperimentalStdlibApi
    override fun onInitialize() {
        println("Common init")


        BountifulContent.register()

    }


}