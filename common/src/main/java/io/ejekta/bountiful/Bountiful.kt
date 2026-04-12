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
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import kotlin.collections.mapIndexed
import kotlin.io.path.appendLines

class Bountiful {
    companion object {
        const val ID = "bountiful"
        const val nightly = false
        var packMode = false
        fun id(str: String) = Identifier.fromNamespaceAndPath(ID, str)
        val LOGGER = Kambrik.Logging.createLogger(ID)

        fun logAndWarn(vararg strs: String) {
            val finStr = strs.mapIndexed { i, str -> if (i > 0) "* $str" else str }.joinToString("\n")
            LOGGER.warn(finStr)
            BountifulIO.errFile.appendText("WRN: $finStr\n")
        }

        fun logAndWarn(strs: List<String>) = logAndWarn(*strs.toTypedArray())

        fun logAndError(vararg strs: String) {
            val finStr = strs.mapIndexed { i, str -> if (i > 0) "* $str" else str }.joinToString("\n")
            LOGGER.error(finStr)
            BountifulIO.errFile.appendText("ERR: $finStr\n")
        }

        val BOUNTY_LOGIC_REGISTRY_KEY: ResourceKey<Registry<IBountyType>> = ResourceKey.createRegistryKey(id("logic_registry"))


        init {
            BountifulIO.emptyErrFile()
        }
    }
}
