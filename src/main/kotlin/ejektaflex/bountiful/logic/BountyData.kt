package ejektaflex.bountiful.logic

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.ext.getSet
import ejektaflex.bountiful.api.ext.setSet
import ejektaflex.bountiful.api.logic.IBountyData
import ejektaflex.bountiful.api.ext.toPretty
import ejektaflex.bountiful.api.ext.toItemStack
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

    override fun boardTimeLeft(world: World): Long {
        return max(boardStamp + Bountiful.config.boardLifespan - world.totalWorldTime , 0)
    }

    fun tooltipInfo(world: World): List<String> {
        return listOf(
                //"Board Time: ${formatTickTime(boardTimeLeft(world) / boardTickFreq)}",
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
        get() = toGet.items.joinToString("§r, ") { "$it" }

    private val rewardPretty: String
        get() = rewards.items.joinToString("§r, ") { "$it" }

    override fun deserializeNBT(tag: NBTTagCompound) {
        boardStamp = tag.getInteger(BountyNBT.BoardStamp.key)
        bountyTime = tag.getLong(BountyNBT.BountyTime.key)
        rarity = tag.getInteger(BountyNBT.Rarity.key)
        worth = tag.getInteger(BountyNBT.Worth.key)
        if (tag.hasKey(BountyNBT.BountyStamp.key)) {
            bountyStamp = tag.getLong(BountyNBT.BountyStamp.key)
        }
        toGet.restore(tag.getSet(BountyNBT.ToGet.key) { PickedEntry() }.toList() )

        rewards.restore(tag.getSet(BountyNBT.Rewards.key) { PickedEntryStack(PickedEntry()) }.toList() )
    }

    override fun serializeNBT(): NBTTagCompound {

        val nGets = NBTTagCompound().apply {
            setSet(BountyNBT.ToGet.key, toGet.items.toSet())
        }


        val nRewards = NBTTagCompound().apply {
            setSet(BountyNBT.Rewards.key, rewards.items.toSet())
        }

        return NBTTagCompound().apply {
            setInteger(BountyNBT.BoardStamp.key, boardStamp)
            setLong(BountyNBT.BountyTime.key, bountyTime)
            setInteger(BountyNBT.Rarity.key, rarity)
            setInteger(BountyNBT.Worth.key, worth)
            bountyStamp?.let { setLong(BountyNBT.BountyStamp.key, it) }
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