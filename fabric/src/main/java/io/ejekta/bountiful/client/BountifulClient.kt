package io.ejekta.bountiful.client

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.mixin.ModelPredicateProviderRegistrar
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

class BountifulClient : ClientModInitializer {

    override fun onInitializeClient() {
        println("Init client for Bountiful")

        Bountybridge.registerItemDynamicTextures()

        ScreenRegistry.register(BountifulContent.BOARD_SCREEN_HANDLER, ::BoardScreen)

    }

}