package ejektaflex.bountiful.logic

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.logic.IBountyData
import ejektaflex.bountiful.api.ext.toPretty
import ejektaflex.bountiful.api.ext.toItemStack
import ejektaflex.bountiful.api.item.IItemBounty
import ejektaflex.bountiful.api.logic.BountyNBT
import ejektaflex.bountiful.item.ItemBounty
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import kotlin.math.max

class BountyData : IBountyData {

    // 72000 = 1 hour IRL
    override var boardTime = Bountiful.config.boardLifespan
    override var bountyTime = 0L
    override var rarity = 0
    override val toGet = mutableListOf<Pair<ItemStack, Int>>()
    override val rewards = mutableListOf<Pair<ItemStack, Int>>()
    override var timestamp: Long? = null
    var worth = 0

    override fun timeLeft(world: World): Long {
        return if (timestamp == null) {
            bountyTime
        } else {
            max(timestamp!! + bountyTime - world.totalWorldTime, 0)
        }
    }

    fun tooltipInfo(world: World): List<String> {
        return listOf(
                "Time To Complete: ${formatTimeExpirable(timeLeft(world) / bountyTickFreq)}",
                "§fRequired: $getPretty",
                "§fRewards: §6$rewardPretty§r"
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
        get() = toGet.joinToString("§r, ") { "§f${it.second}x §a${it.first.displayName}" }

    private val rewardPretty: String
        get() = rewards.joinToString("§r, ") { "§f${it.second}x §6${it.first.displayName}" }

    override fun deserializeNBT(tag: NBTTagCompound) {
        boardTime = tag.getInteger(BountyNBT.BoardTime.key)
        bountyTime = tag.getLong(BountyNBT.BountyTime.key)
        rarity = tag.getInteger(BountyNBT.Rarity.key)
        worth = tag.getInteger(BountyNBT.Worth.key)
        if (tag.hasKey(BountyNBT.TimeStamp.key)) {
            timestamp = tag.getLong(BountyNBT.TimeStamp.key)
        }
        toGet.clear()
        for (gettable in tag.getCompoundTag(BountyNBT.ToGet.key).keySet) {
            toGet.add(gettable.toItemStack!! to tag.getCompoundTag(BountyNBT.ToGet.key).getInteger(gettable))
        }
        rewards.clear()
        for (gettable in tag.getCompoundTag(BountyNBT.Rewards.key).keySet) {
            rewards.add(gettable.toItemStack!! to tag.getCompoundTag(BountyNBT.Rewards.key).getInteger(gettable))
        }
    }

    override fun serializeNBT(): NBTTagCompound {

        val nGets = NBTTagCompound().apply {
            for (pair in toGet) {
                setInteger(pair.first.toPretty, pair.second)
            }
        }

        val nRewards = NBTTagCompound().apply {
            for (pair in rewards) {
                setInteger(pair.first.toPretty, pair.second)
            }
        }

        return NBTTagCompound().apply {
            setInteger(BountyNBT.BoardTime.key, boardTime)
            setLong(BountyNBT.BountyTime.key, bountyTime)
            setInteger(BountyNBT.Rarity.key, rarity)
            setInteger(BountyNBT.Worth.key, worth)
            timestamp?.let { setLong(BountyNBT.TimeStamp.key, it) }
            setTag(BountyNBT.ToGet.key, nGets)
            setTag(BountyNBT.Rewards.key, nRewards)
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