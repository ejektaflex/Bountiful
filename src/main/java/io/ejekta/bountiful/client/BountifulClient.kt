package io.ejekta.bountiful.client

import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.bounty.logic.BountyRarity
import io.ejekta.bountiful.common.content.BountifulContent
import io.ejekta.bountiful.common.mixin.ModelPredicateProviderRegistrar
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.ModelPredicateProvider
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class BountifulClient : ClientModInitializer {

    private val mc = MinecraftClient.getInstance()

    override fun onInitializeClient() {
        println("Init client for Bountiful")

        ModelPredicateProviderRegistrar.registerInvoker(
            BountifulContent.BOUNTY_ITEM,
            Identifier(Bountiful.ID, "rarity")
        ) { itemStack: ItemStack, clientWorld: ClientWorld?, livingEntity: LivingEntity? ->
            val rarity = BountyData[itemStack].rarity
            println("RARITY: $rarity")
            rarity.ordinal.toFloat() / 10f
        }

        println("Registered rarity property")

    }

}