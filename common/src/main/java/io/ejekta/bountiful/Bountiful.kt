package io.ejekta.bountiful

import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.text.broadcastSystemMessage
import io.ejekta.kambrik.text.sendMessage
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.ChatFormatting
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer

class Bountiful {
    companion object {
        const val ID = "bountiful"
        const val nightly = true
        var packMode = false
        fun id(str: String) = ResourceLocation.fromNamespaceAndPath(ID, str)
        val LOGGER = Kambrik.Logging.createLogger(ID)

        fun MinecraftServer.logAndWarn(str: String) {
            LOGGER.warn(str)
            if (packMode) {
                playerList.players.forEach { player ->
                    player.sendSystemMessage(
                        textLiteral("Warning: $str").withStyle(ChatFormatting.GOLD)
                    )
                }
            }
        }

        fun MinecraftServer.logAndError(str: String) {
            LOGGER.warn(str)
            if (packMode) {
                sendSystemMessage(
                    textLiteral("Error: $str").withStyle(ChatFormatting.RED)
                )
                sendSystemMessage(
                    textLiteral("See log for details.").withStyle(ChatFormatting.RED)
                )
            }
        }

        val BOUNTY_LOGIC_REGISTRY_KEY: ResourceKey<Registry<IBountyType>> = ResourceKey.createRegistryKey(id("logic_registry"))
    }
}