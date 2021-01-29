package io.ejekta.bountiful.common.content

import io.ejekta.bountiful.common.bounty.BountyData
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
import net.minecraft.util.Rarity
import net.minecraft.world.World
import org.spongepowered.asm.mixin.MixinEnvironment

class BountyItem : Item(
    FabricItemSettings()
        .maxCount(1)
        .fireproof()
        .group(ItemGroup.MISC)
) {

    override fun getName(stack: ItemStack?): Text {
        return if (stack != null && clientWorld() != null) {
            val data = BountyData[stack]
            TranslatableText(data.rarity.name.toLowerCase().capitalize() + " Bounty (").append(
                data.formattedTimeLeft(clientWorld()!!)
            ).append(LiteralText(")"))
        } else {
            LiteralText("No Bounty Stack Given")
        }
    }

    override fun getRarity(stack: ItemStack?): Rarity {
        return stack?.let {
            BountyData[it].rarity
        } ?: Rarity.COMMON
    }

    override fun appendTooltip(
        stack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext?
    ) {
        if (stack != null && world != null) {
            val data = BountyData[stack].tooltipInfo(world)
            tooltip.addAll(data)
        }
        super.appendTooltip(stack, world, tooltip, context)
    }

    companion object {

        fun create(): ItemStack {

            return ItemStack(BountifulContent.BOUNTY_ITEM).apply {
                BountyData.set(this, BountyData())
            }


        }

    }

}