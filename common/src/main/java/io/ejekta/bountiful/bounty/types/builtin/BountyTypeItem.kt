package io.ejekta.bountiful.bounty.types.builtin

import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.types.IBountyExchangeable
import io.ejekta.bountiful.bounty.types.Progress
import io.ejekta.bountiful.bridge.GsonObject
import io.ejekta.bountiful.components.BountyDataEntry
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.util.getTagItemKey
import io.ejekta.bountiful.util.getTagItems
import io.ejekta.kambrik.bridge.Kambridge
import io.ejekta.kambrik.ext.collect
import io.ejekta.kambrik.ext.id
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.ChatFormatting
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.EnchantedBookItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.enchantment.EnchantmentHelper
import kotlin.jvm.optionals.getOrNull


class BountyTypeItem : IBountyExchangeable {

    override val id: ResourceLocation = ResourceLocation.parse("item")

    override fun isValid(entry: PoolEntry, server: MinecraftServer): Boolean {
        return if (entry.content.startsWith("#")) {
            getTagItems(server.registryAccess(), getTagItemKey(
                ResourceLocation.parse(entry.content.substringAfter("#"))
            )).isNotEmpty()
        } else {
            val id = getItem(ResourceLocation.parse(entry.content)).id
            id == ResourceLocation.parse(entry.content)
        }
    }

    private fun getCurrentStacks(entry: BountyDataEntry, player: Player): Map<ItemStack, Int> {
        return player.inventory.items.collect(entry.amount) {
            id.toString() == entry.content
        }
    }

    override fun textOnBounty(entry: BountyDataEntry, isObj: Boolean, player: Player, current: Int): List<MutableComponent> {
        val progress = getProgress(entry, player, current)
        val itemName = getItemName(entry, player.level().registryAccess())
        val itemLine = itemName[0].copy()
        val result = when (isObj) {
            true -> itemLine.withStyle(progress.color).append(progress.neededText.colored(ChatFormatting.WHITE))
            false -> progress.givingText.append(itemLine.colored(entry.rarity.color))
        }
        return listOf(result) + itemName.drop(1)
    }

    override fun textOnBoardSidebar(entry: BountyDataEntry, player: Player): List<Component> {
        return getItemStack(entry, player.level().registryAccess()).getTooltipLines(Item.TooltipContext.EMPTY, player, TooltipFlag.NORMAL)
    }

    override fun getProgress(entry: BountyDataEntry, player: Player, current: Int): Progress {
        return Progress(getCurrentStacks(entry, player).values.sum(), entry.amount)
    }

    override fun getNewCurrent(entry: BountyDataEntry, player: Player, current: Int): Int {
        return getCurrentStacks(entry, player).values.sum()
    }

    override fun consumeObjectives(entry: BountyDataEntry, player: Player, current: Int): Boolean {
        val currStacks = getCurrentStacks(entry, player)
        if (currStacks.values.sum() >= entry.amount) {
            currStacks.forEach { (stack, toShrink) ->
                stack.shrink(toShrink)
            }
            return true
        }
        return false
    }

    override fun giveReward(entry: BountyDataEntry, player: Player) {
        val item = getItem(entry)
        val toGive = (0 until entry.amount).chunked(item.defaultMaxStackSize).map { it.size }

        for (amtToGive in toGive) {

            val stack = when (val componentData = entry.data) {
                null -> {
                    ItemStack(item, amtToGive)
                }
                else -> {
                    val gson = GsonObject().apply {
                        addProperty("id",  item.id.toString())
                        addProperty("count", amtToGive)
                        add("components", componentData)
                    }

                    val ops = RegistryOps.create(JsonOps.INSTANCE, player.registryAccess())
                    val stackResult = ItemStack.CODEC.decode(ops, gson)
                    val result = stackResult.result().getOrNull()

                    if (result == null) {
                        Bountiful.LOGGER.warn("Decoding item to give resulted in null")
                        continue
                    }

                    result.first
                }
            }

            // Try give directly to player, otherwise drop at feet
            if (!player.addItem(stack)) {
                val pos = player.position()
                val stackEntity = ItemEntity(player.level(), pos.x, pos.y, pos.z, stack).apply {
                    setPickUpDelay(0)
                }
                player.level().addFreshEntity(stackEntity)
            }
        }
    }

    companion object {
        fun getItem(entry: BountyDataEntry): Item {
            return getItem(ResourceLocation.parse(entry.content))
        }

        fun getItem(id: ResourceLocation): Item {
            return BuiltInRegistries.ITEM.get(id)
        }

        fun getItemStack(entry: BountyDataEntry, access: RegistryAccess): ItemStack {
            val item = getItem(entry)
            val regOps = RegistryOps.create(JsonOps.INSTANCE, access)

            val built = JsonObject().apply {
                addProperty("id", item.id.toString())
                addProperty("count", entry.amount)
                add("components", entry.data)
            }
            val itemDone = ItemStack.CODEC.decode(regOps, built).result()
            return itemDone.getOrNull()?.first ?: ItemStack(Items.STICK)
        }

        fun getItemName(entry: BountyDataEntry, access: RegistryAccess): List<MutableComponent> {
            val itemStack = getItemStack(entry, access)
            var named = mutableListOf(itemStack.displayName.copy())

            if (itemStack.item is EnchantedBookItem && Kambridge.isOnClient()) {
                val lines = itemStack.getTooltipLines(Item.TooltipContext.of(access), null, TooltipFlag.NORMAL)
                val enchants = EnchantmentHelper.getEnchantmentsForCrafting(itemStack)
                if (enchants.size() > 0 && lines.size > 1) {
                    val allEnchantsComponent = lines.drop(1).map { it }.map {
                        textLiteral("* ").append(it).withStyle(ChatFormatting.DARK_GRAY)
                    }

                    named.addAll(allEnchantsComponent)
                }
            }

            return named
        }
    }

}