package io.ejekta.bountiful.bounty.types.builtin

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.types.IBountyExchangeable
import io.ejekta.bountiful.bounty.types.Progress
import io.ejekta.bountiful.bridge.GsonObject
import io.ejekta.bountiful.components.BountyDataEntry
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.util.asComponentJson
import io.ejekta.bountiful.util.getTagItemKey
import io.ejekta.bountiful.util.getTagItems
import io.ejekta.bountiful.util.isJsonSubset
import io.ejekta.kambrik.bridge.Kambridge
import io.ejekta.kambrik.ext.collect
import io.ejekta.kambrik.ext.id
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.ChatFormatting
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.enchantment.EnchantmentHelper
import kotlin.jvm.optionals.getOrNull


class BountyTypeItem : IBountyExchangeable {

    override val id: Identifier = Identifier.parse("item")

    override fun isValid(entry: PoolEntry, server: MinecraftServer): Boolean {
        return if (entry.content.startsWith("#")) {
            getTagItems(getTagItemKey(
                Identifier.parse(entry.content.substringAfter("#"))
            )).isNotEmpty()
        } else {
            val id = getItem(Identifier.parse(entry.content)).id
            id == Identifier.parse(entry.content)
        }
    }

    private fun getCurrentStacks(entry: BountyDataEntry, player: Player): Map<ItemStack, Int> {
        return player.inventory.getNonEquipmentItems().collect(entry.amount) {
            val sameId = id.toString() == entry.content
            if (entry.data == null) {
                return@collect sameId // only do id check
            } else if (!sameId) {
                return@collect false // failed id check
            }

            val itemJson = asComponentJson(player.registryAccess())
            val reqJson = entry.data!!

            // If component requirements is empty, then of course it matches
            if (reqJson?.keySet()?.isEmpty() == true) {
                return@collect true
            }

            return@collect isJsonSubset(reqJson, itemJson)
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
        val toGive = (0 until entry.amount).chunked(ItemStack(item).maxStackSize).map { it.size }

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
            return getItem(Identifier.parse(entry.content))
        }

        fun getItem(id: Identifier): Item {
            return BuiltInRegistries.ITEM.getOptional(id).orElse(null) ?: Items.AIR
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
            var named = mutableListOf<MutableComponent>(itemStack.hoverName.copy())

            if (Kambridge.isOnClient()) {
                var extra = mutableListOf<MutableComponent>()
                val enchantComponent = itemStack.get(DataComponents.ENCHANTMENTS).takeUnless { it?.isEmpty == true }
                    ?: itemStack.get(DataComponents.STORED_ENCHANTMENTS)
                enchantComponent?.let { ec ->
                    val extraCast = extra as MutableList<Component>
                    ec.addToTooltip(Item.TooltipContext.of(access), extraCast::add, TooltipFlag.NORMAL, itemStack)
                    for (extraTip in extra) {
                        named.add(
                            textLiteral("* ").append(extraTip)
                        )
                    }
                }
            }

            return named
        }
    }

}
