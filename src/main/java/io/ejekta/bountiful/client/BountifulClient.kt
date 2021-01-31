package io.ejekta.bountiful.client

import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.content.gui.BoardScreen
import io.ejekta.bountiful.common.content.BountifulContent
import io.ejekta.bountiful.common.mixin.ModelPredicateProviderRegistrar
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

class BountifulClient : ClientModInitializer {

    private val mc = MinecraftClient.getInstance()

    override fun onInitializeClient() {
        println("Init client for Bountiful")

        ModelPredicateProviderRegistrar.registerInvoker(
            BountifulContent.BOUNTY_ITEM,
            Identifier(Bountiful.ID, "rarity")
        ) { itemStack: ItemStack, _: ClientWorld?, _: LivingEntity? ->
            val rarity = BountyData[itemStack].rarity
            rarity.ordinal.toFloat() / 10f
        }

        ClientPlayNetworking.registerGlobalReceiver(
            Bountiful.id("copydata")
        ) { minecraftClient, _, packetByteBuf, _ ->
            val str = packetByteBuf.readString()
            minecraftClient.execute {
                minecraftClient.keyboard.clipboard = str
            }
        }


        ScreenRegistry.register(BountifulContent.BOARD_SCREEN_HANDLER, ::BoardScreen)

        println("Registered rarity property")

    }

}