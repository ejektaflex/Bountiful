package io.ejekta.bountiful

import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.text.broadcastSystemMessage
import io.ejekta.kambrik.text.sendMessage
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.ChatFormatting
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import kotlin.io.path.appendLines

class Bountiful {
    companion object {
        const val ID = "bountiful"
        const val nightly = true
        var packMode = false
        fun id(str: String) = ResourceLocation.fromNamespaceAndPath(ID, str)
        val LOGGER = Kambrik.Logging.createLogger(ID)

        fun logAndWarn(str: String) {
            LOGGER.warn(str)
            BountifulIO.errFile.appendText("WRN: $str\n")
        }

        fun logAndError(str: String) {
            LOGGER.error(str)
            BountifulIO.errFile.appendText("ERR: $str\n")
        }

        val BOUNTY_LOGIC_REGISTRY_KEY: ResourceKey<Registry<IBountyType>> = ResourceKey.createRegistryKey(id("logic_registry"))


        init {
            BountifulIO.emptyErrFile()
        }
    }
}