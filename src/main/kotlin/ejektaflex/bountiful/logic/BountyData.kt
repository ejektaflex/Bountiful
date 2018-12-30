package ejektaflex.bountiful.logic

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.ext.getPickedEntryList
import ejektaflex.bountiful.api.ext.getUnsortedList
import ejektaflex.bountiful.api.ext.setUnsortedList
import ejektaflex.bountiful.api.logic.IBountyData
import ejektaflex.bountiful.api.item.IItemBounty
import ejektaflex.bountiful.api.logic.BountyNBT
import ejektaflex.bountiful.api.logic.pickable.IPickedEntry
import ejektaflex.bountiful.api.logic.pickable.PickedEntry
import ejektaflex.bountiful.api.logic.pickable.PickedEntryStack
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.registry.ValueRegistry
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import kotlin.math.max

class BountyData : IBountyData {

    // 72000 = 1 hour IRL
    override var boardStamp = Bountiful.config.boardLifespan
    override var bountyTime = 0L
    override var rarity = 0
    override val toGet = ValueRegistry<IPickedEntry>()
    override val rewards = ValueRegistry<PickedEntryStack>()
    override var bountyStamp: Long? = null
    var worth = 0

    override fun timeLeft(world: World): Long {
        return if (bountyStamp == null) {
            bountyTime
        } else {
            max(bountyStamp!! + bountyTime - world.totalWorldTime, 0)
        }
    }

    override fun hasExpired(world: World): Boolean {
        return timeLeft(world) <= 0
    }

    override fun boardTimeLeft(world: World): Long {
        return max(boardStamp + Bountiful.config.boardLifespan - world.totalWorldTime , 0)
    }

    fun tooltipInfo(world: World): List<String> {
        return listOf(
                //"Board Time: ${formatTickTime(boardTimeLeft(world) / boardTickFreq)}",
                "Time To Complete: ${formatTimeExpirable(timeLeft(world) / bountyTickFreq)}",
                getPretty,
                "§fRewards: $rewardPretty"
        )
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
            "§4Expired"
        } else {
            formatTickTime(n)
        }
    }

    private val getPretty: String
        get() {
            return if (toGet.items.isEmpty()) {
                "§6Completed. §aTurn it in!"
            } else {
                "§fRequired: " + toGet.items.joinToString(", ") {
                    "§f${it.amount}x §a${it.prettyContent}§f"
                } + "§r"
            }

        }

    private val rewardPretty: String
        get() {
            return rewards.items.joinToString(", ") {
                "§f${it.amount}x §6${it.prettyContent}§f"
            } + "§r"
        }

    override fun deserializeNBT(tag: NBTTagCompound) {
        boardStamp = tag.getInteger(BountyNBT.BoardStamp.key)
        bountyTime = tag.getLong(BountyNBT.BountyTime.key)
        rarity = tag.getInteger(BountyNBT.Rarity.key)
        worth = tag.getInteger(BountyNBT.Worth.key)
        if (tag.hasKey(BountyNBT.BountyStamp.key)) {
            bountyStamp = tag.getLong(BountyNBT.BountyStamp.key)
        }
        toGet.restore(
                tag.getPickedEntryList(BountyNBT.ToGet.key).toList()
        )

        rewards.restore(tag.getUnsortedList(BountyNBT.Rewards.key) { PickedEntryStack(PickedEntry()) }.toList() )
    }

    override fun serializeNBT(): NBTTagCompound {

        return NBTTagCompound().apply {
            setInteger(BountyNBT.BoardStamp.key, boardStamp)
            setLong(BountyNBT.BountyTime.key, bountyTime)
            setInteger(BountyNBT.Rarity.key, rarity)
            setInteger(BountyNBT.Worth.key, worth)
            bountyStamp?.let { setLong(BountyNBT.BountyStamp.key, it) }
            setUnsortedList(BountyNBT.ToGet.key, toGet.items.toSet())
            setUnsortedList(BountyNBT.Rewards.key, rewards.items.toSet())
        }
    }

    companion object {
        const val bountyTickFreq = 20L
        const val boardTickFreq = 20L

        fun from(stack: ItemStack): IBountyData {
            if (stack.item is ItemBounty) {
                return (stack.item as IItemBounty).getBountyData(stack)
            } else {
                throw Exception("${stack.displayName} is not an IItemBounty and cannot be converted to bounty data!")
            }
        }

    }

}