package io.ejekta.bountiful.client

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.messages.ClipboardCopy
import io.ejekta.bountiful.content.messages.UpdateBountyCriteriaObjective
import io.ejekta.bountiful.content.messages.UpdateBountyTooltipNotification
import io.ejekta.bountiful.mixin.ModelPredicateProviderRegistrar
import io.ejekta.kambrik.Kambrik
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

class BountifulClient : ClientModInitializer {

    override fun onInitializeClient() {
        println("Init client for Bountiful")

        ModelPredicateProviderRegistrar.registerInvoker(
            BountifulContent.BOUNTY_ITEM,
            Bountiful.id("rarity")
        ) { itemStack: ItemStack, _: ClientWorld?, _: LivingEntity?, _: Int ->
            val rarity = BountyInfo[itemStack].rarity
            rarity.ordinal.toFloat() / 10f
        }

        ModelPredicateProviderRegistrar.registerInvoker(
            BountifulContent.DECREE_ITEM,
            Bountiful.id("status")
        ) { itemStack: ItemStack, _: ClientWorld?, _: LivingEntity?, _: Int ->
            val data = DecreeData[itemStack]
            if (data.ids.isNotEmpty()) 1f else 0f
        }



        ScreenRegistry.register(BountifulContent.BOARD_SCREEN_HANDLER, ::BoardScreen)

    }

}