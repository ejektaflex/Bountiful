@file:UseSerializers(IdentitySer::class)
package io.ejekta.bountiful.common

import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.bounty.logic.BountyDataEntry
import io.ejekta.bountiful.common.bounty.logic.BountyRarity
import io.ejekta.bountiful.common.bounty.logic.BountyType
import io.ejekta.bountiful.common.config.BountyReloadListener
import io.ejekta.bountiful.common.content.BountifulContent
import io.ejekta.bountiful.common.serial.IdentitySer
import kotlinx.serialization.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.nbt.*
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import kotlin.reflect.typeOf

class Bountiful : ModInitializer {

    companion object {
        val ID = "bountiful"
        fun id(str: String) = Identifier(ID, str)
    }

    init {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(BountyReloadListener())
    }

    @ExperimentalStdlibApi
    inline fun <reified T : Any> doot() {
        println("DOOT: ${typeOf<T>()}")
    }

    @ExperimentalSerializationApi
    @ExperimentalStdlibApi
    override fun onInitialize() {
        println("Common init")

        val durability = CompoundTag().apply {
            put("Durability", LongArrayTag(listOf(1L, 2L, 3L)))
        }

        val bd = BountyData().apply {
            timeStarted = 100
            timeToComplete = 300
            rarity = BountyRarity.EPIC
            objectives.add(
                BountyDataEntry(BountyType.ITEM, "minecraft:dirt", 2)
            )
            rewards.add(
                BountyDataEntry(BountyType.ITEM, "minecraft:iron_ingot", 10).apply {
                    nbtData = durability
                }
            )
        }

        BountifulContent.register()

    }






}