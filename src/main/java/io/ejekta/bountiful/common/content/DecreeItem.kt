package io.ejekta.bountiful.common.content

import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.bounty.logic.BountyRarity
import io.ejekta.bountiful.common.bounty.logic.DecreeList
import io.ejekta.bountiful.common.util.clientWorld
import io.ejekta.bountiful.common.util.isClient
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.MinecraftClientGame
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Rarity
import net.minecraft.world.World
import org.spongepowered.asm.mixin.MixinEnvironment

class DecreeItem : Item(
    FabricItemSettings()
        .maxCount(1)
        .fireproof()
        .group(ItemGroup.MISC)
) {

    override fun getTranslationKey() = "bountiful.decree"

    override fun getName(stack: ItemStack?): Text {
        return TranslatableText(translationKey).formatted(Formatting.DARK_PURPLE)
    }

    override fun appendTooltip(
        stack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext?
    ) {
        if (stack != null && world != null) {
            val data = DecreeList[stack].tooltipInfo(world)
            tooltip.addAll(data)
        }
        super.appendTooltip(stack, world, tooltip, context)
    }

    companion object {

        fun create(data: DecreeList? = null): ItemStack {
            return ItemStack(BountifulContent.DECREE_ITEM).apply {
                DecreeList[this] = data ?: DecreeList(
                    mutableListOf(
                        BountifulContent.Decrees.random().id
                    )
                )
            }
        }

    }

}