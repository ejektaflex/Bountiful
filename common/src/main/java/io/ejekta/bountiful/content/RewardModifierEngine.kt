package io.ejekta.bountiful.content

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeItem
import io.ejekta.bountiful.components.BountyDataEntry
import io.ejekta.bountiful.components.GsonObject
import io.ejekta.bountiful.data.BountyModifier
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.util.asComponentJson
import io.ejekta.kambrik.ext.id
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import java.util.Optional
import kotlin.jvm.optionals.getOrNull
import kotlin.math.max
import kotlin.math.min

object RewardModifierEngine {

    data class ModifiedReward(
        val components: GsonObject?,
        val rarity: BountyRarity,
        val worth: Double
    )

    fun apply(
        world: ServerLevel,
        entry: PoolEntry,
        actualContent: String,
        amount: Int,
        baseWorth: Double
    ): ModifiedReward? {
        var stack = BountyTypeItem.getItemStack(
            BountyDataEntry(
                id = entry.id,
                content = actualContent,
                rarity = entry.rarity,
                logicName = entry.type.toString(),
                amount = amount,
                name = entry.name,
                data = entry.components
            ),
            world.registryAccess()
        )

        if (stack.`is`(Items.AIR)) {
            return null
        }

        var bestRarity = entry.rarity
        var worth = baseWorth
        var touched = false

        for (modifierId in entry.modifiers) {
            val modifier = BountifulContent.ModifierMap[modifierId] ?: continue
            if (world.random.nextDouble() > modifier.clampedChance) {
                continue
            }

            when (modifier.type) {
                BountyModifier.Type.ENCHANTS -> {
                    val result = applyEnchantModifier(world, stack, modifier, worth) ?: continue
                    stack = result.first
                    bestRarity = maxOf(bestRarity, result.second)
                    worth = result.third
                    touched = true
                }
            }
        }

        if (!touched) {
            return null
        }

        return ModifiedReward(stack.asComponentJson(world.registryAccess()), bestRarity, worth)
    }

    private fun applyEnchantModifier(
        world: ServerLevel,
        stack: ItemStack,
        modifier: BountyModifier,
        baseWorth: Double
    ): Triple<ItemStack, BountyRarity, Double>? {
        if (!canApplyToItem(stack, modifier)) {
            return null
        }

        val registry = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
        val optionHolders = modifier.options.mapNotNull { registry.get(it).getOrNull() }
        val optionSet = optionHolders.takeIf { it.isNotEmpty() }?.let { OptionalHolderSet.direct(it) } ?: Optional.empty()

        val enchanted = EnchantmentHelper.enchantItem(
            world.random,
            stack.copy(),
            modifier.levels.pick(),
            world.registryAccess(),
            optionSet
        )

        if (!enchanted.isEnchanted) {
            return null
        }

        var rarity = BountyRarity.COMMON
        var flatBonus = 0.0
        var multBonus = 0.0

        val enchantments = enchanted.enchantments.entrySet()
        for ((holder, level) in enchantments) {
            val enchantment = holder.value()
            val definition = enchantment.definition()
            val enchantRarity = modifier.rarityForWeight(definition.weight())
            rarity = maxOf(rarity, enchantRarity)

            flatBonus += definition.anvilCost() * level * modifier.valueFlatScale

            val avgCost = (definition.minCost().calculate(level) + definition.maxCost().calculate(level)) / 2.0
            val levelRatio = level.toDouble() / definition.maxLevel().coerceAtLeast(1).toDouble()
            multBonus += avgCost * modifier.valueCostScale * levelRatio
        }

        val reratedWorth = max(
            1.0,
            (baseWorth + flatBonus) * (1.0 + min(multBonus, 2.5))
        )

        return Triple(enchanted, rarity, reratedWorth)
    }

    private fun canApplyToItem(stack: ItemStack, modifier: BountyModifier): Boolean {
        if (modifier.applicableItems.isNotEmpty() && stack.item.id !in modifier.applicableItems) {
            return false
        }

        if (modifier.applicableTags.isNotEmpty()) {
            val matchesTag = modifier.applicableTags.any {
                stack.`is`(TagKey.create(BuiltInRegistries.ITEM.key(), it))
            }
            if (!matchesTag) {
                return false
            }
        }

        if (modifier.onlyCompatible) {
            return stack.isEnchantable || stack.`is`(Items.BOOK) || stack.isEnchanted
        }

        return true
    }

    private object OptionalHolderSet {
        fun direct(holders: List<Holder<Enchantment>>): Optional<HolderSet<Enchantment>> {
            return Optional.of(HolderSet.direct(holders))
        }
    }
}
