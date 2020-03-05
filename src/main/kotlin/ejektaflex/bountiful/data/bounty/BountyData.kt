package ejektaflex.bountiful.data.bounty

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.data.bounty.enums.BountyNBT
import ejektaflex.bountiful.data.bounty.enums.BountyRarity
import ejektaflex.bountiful.ext.setUnsortedList
import ejektaflex.bountiful.ext.getUnsortedList
import ejektaflex.bountiful.ext.toBountyEntry
import ejektaflex.bountiful.util.ValueRegistry
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.logic.BountyTypeRegistry
import ejektaflex.bountiful.logic.IBountyObjective
import ejektaflex.bountiful.logic.IBountyReward
import ejektaflex.bountiful.data.bounty.checkers.CheckerRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable
import kotlin.math.max

class BountyData : INBTSerializable<CompoundNBT> {

    var boardStamp = BountifulMod.config.boardLifespan
    var bountyTime = 0L
    var rarity = 0
    val objectives = ValueRegistry<BountyEntry>()
    val rewards = ValueRegistry<BountyEntry>()
    var bountyStamp: Long? = null

    fun timeLeft(world: World): Long {
        return if (bountyStamp == null) {
            bountyTime
        } else {
            max(bountyStamp!! + bountyTime - world.gameTime, 0)
        }
    }

    fun hasExpired(world: World): Boolean {
        return timeLeft(world) <= 0
    }

    val rarityEnum: BountyRarity
        get() = BountyRarity.getRarityFromInt(rarity)

    fun boardTimeLeft(world: World): Long {
        return max(boardStamp + BountifulMod.config.boardLifespan - world.gameTime , 0)
    }


    fun tooltipInfo(world: World, advanced: Boolean): List<ITextComponent> {
        val passed = CheckerRegistry.passedChecks(Minecraft.getInstance().player!!, this)

        val objs = passed.toList().sortedBy {
            BountyTypeRegistry.content.indexOf(it.first.type)
        }.map {
            (it.first as IBountyObjective).tooltipObjective(it.second)
        }

        val rews = rewards.content.map {
            (it as IBountyReward).tooltipReward()
        }


        return listOf(
                //"Board Time: ${formatTickTime(boardTimeLeft(world) / boardTickFreq)}",
                listOf(
                        StringTextComponent("§6").appendSibling(
                            TranslationTextComponent("bountiful.tooltip.required")
                        ).appendSibling(
                                StringTextComponent(":§f ")
                        )
                ) +
                objs +
                listOf(
                        StringTextComponent("§6").appendSibling(
                                TranslationTextComponent("bountiful.tooltip.rewards")
                        ).appendSibling(
                                StringTextComponent(":§f ")
                        )
                ) +
                rews +
                listOf(
                        //TranslationTextComponent("bountiful.tooltip.advanced")
                )
        ).flatten()

    }

    fun remainingTime(world: World): String {
        return formatTimeExpirable(timeLeft(world) / bountyTickFreq)
    }

    private fun formatTickTime(n: Long): String {
        return if (n / 60 <= 0) {
            "${n}s"
        } else {
            "${n / 60}m ${n % 60}s"
        }
    }

    private fun formatTimeExpirable(n: Long): String {
        return if (n <= 0) {
            "§4${I18n.format("bountiful.tooltip.expired")}"
        } else {
            formatTickTime(n)
        }
    }



    override fun deserializeNBT(tag: CompoundNBT) {
        boardStamp = tag.getInt(BountyNBT.BoardStamp.key)
        bountyTime = tag.getLong(BountyNBT.BountyTime.key)
        rarity = tag.getInt(BountyNBT.Rarity.key)

        if (BountyNBT.BountyStamp.key in tag) {
            bountyStamp = tag.getLong(BountyNBT.BountyStamp.key)
        }

        objectives.restore(
                tag.getUnsortedList(BountyNBT.Objectives.key).map { it.toBountyEntry }
        )

        rewards.restore(
                tag.getUnsortedList(BountyNBT.Rewards.key).map { it.toBountyEntry }
        )
    }

    override fun serializeNBT(): CompoundNBT {
        return CompoundNBT().apply {
            putInt(BountyNBT.BoardStamp.key, boardStamp)
            putLong(BountyNBT.BountyTime.key, bountyTime)
            putInt(BountyNBT.Rarity.key, rarity)
            bountyStamp?.let { putLong(BountyNBT.BountyStamp.key, it) }
            setUnsortedList(BountyNBT.Objectives.key, objectives.content.toSet())
            setUnsortedList(BountyNBT.Rewards.key, rewards.content.toSet())
        }
    }

    companion object {
        const val bountyTickFreq = 20L
        const val boardTickFreq = 20L

        fun isValidBounty(stack: ItemStack): Boolean {
            return try {
                from(stack)
                true
            } catch (e: Exception) {
                false
            }
        }

        fun from(stack: ItemStack): BountyData {
            if (stack.item is ItemBounty) {
                return (stack.item as ItemBounty).getBountyData(stack) as BountyData
            } else {
                throw Exception("${stack.displayName} is not an IItemBounty and cannot be converted to bounty data!")
            }
        }

        fun safeData(stack: ItemStack): BountyData? {
            return try {
                from(stack)
            } catch (e: Exception) {
                null
            }
        }

    }

}