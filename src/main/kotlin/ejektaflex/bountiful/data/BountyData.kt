package ejektaflex.bountiful.data

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.api.ext.setUnsortedList
import ejektaflex.bountiful.api.data.IBountyData
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.api.ext.getUnsortedList
import ejektaflex.bountiful.api.ext.toBountyEntry
import ejektaflex.bountiful.api.item.IItemBounty
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.logic.BountyTypeRegistry
import ejektaflex.bountiful.logic.IBountyObjective
import ejektaflex.bountiful.logic.IBountyReward
import ejektaflex.bountiful.logic.checkers.CheckerRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.world.World
import kotlin.math.max

class BountyData : IBountyData {

    override var boardStamp = BountifulMod.config.boardLifespan
    override var bountyTime = 0L
    override var rarity = 0
    override val objectives = ValueRegistry<BountyEntry>()
    override val rewards = ValueRegistry<BountyEntry>()
    override var bountyStamp: Long? = null

    override fun timeLeft(world: World): Long {
        return if (bountyStamp == null) {
            bountyTime
        } else {
            max(bountyStamp!! + bountyTime - world.gameTime, 0)
        }
    }

    override fun hasExpired(world: World): Boolean {
        return timeLeft(world) <= 0
    }


    override fun boardTimeLeft(world: World): Long {
        return max(boardStamp + BountifulMod.config.boardLifespan - world.gameTime , 0)
    }

    fun tooltipInfo(world: World, advanced: Boolean): List<String> {
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
                "${I18n.format("bountiful.tooltip.time")}: ${formatTimeExpirable(timeLeft(world) / bountyTickFreq)}") +
                listOf("§6${I18n.format("bountiful.tooltip.required")}:§f ") +
                objs +
                listOf("§6${I18n.format("bountiful.tooltip.rewards")}:§f ") +
                rews +
                listOf(I18n.format("bountiful.tooltip.advanced") )

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
                return (stack.item as IItemBounty).getBountyData(stack) as BountyData
            } else {
                throw Exception("${stack.displayName} is not an IItemBounty and cannot be converted to bounty data!")
            }
        }

    }

}