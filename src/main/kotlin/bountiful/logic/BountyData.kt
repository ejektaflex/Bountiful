package bountiful.logic

import bountiful.Bountiful
import bountiful.ext.toPretty
import bountiful.ext.toItemStack
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

class BountyData : INBTSerializable<NBTTagCompound> {

    // 72000 = 1 hour IRL
    private var boardTime = Bountiful.config.boardLifespan
    var time = 0L
    var rarity = 0
    val toGet = mutableListOf<Pair<ItemStack, Int>>()
    val rewards = mutableListOf<Pair<ItemStack, Int>>()
    var worth = 0

    override fun toString(): String {
        return "" +
                //"Board Time: ${formatTickTime(boardTime / BountyData.boardTickFreq)}\n" +
                "Time To Complete: ${formatTickTime(time / BountyData.bountyTickFreq)}\n" +
                "§fRequired: $getPretty\n§fRewards: §6$rewardPretty§r"
    }

    private fun formatTickTime(n: Long): String {
        return if (n / 60 <= 0) {
            "${n}s"
        } else {
            "${n / 60}m ${n % 60}s"
        }
    }

    private val getPretty: String
        get() = toGet.joinToString("§r, ") { "§f${it.second}x §a${it.first.displayName}" }

    private val rewardPretty: String
        get() = rewards.joinToString("§r, ") { "§f${it.second}x §6${it.first.displayName}" }

    override fun deserializeNBT(tag: NBTTagCompound) {
        boardTime = tag.getInteger("boardTime")
        time = tag.getLong("time")
        rarity = tag.getInteger("rarity")
        worth = tag.getInteger("worth")
        toGet.clear()
        for (gettable in tag.getCompoundTag("gets").keySet) {
            toGet.add(gettable.toItemStack!! to tag.getCompoundTag("gets").getInteger(gettable))
        }
        rewards.clear()
        for (gettable in tag.getCompoundTag("rewards").keySet) {
            rewards.add(gettable.toItemStack!! to tag.getCompoundTag("rewards").getInteger(gettable))
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
            setInteger("boardTime", boardTime)
            setLong("time", time)
            setInteger("rarity", rarity)
            setInteger("worth", worth)
            setTag("gets", nGets)
            setTag("rewards", nRewards)
        }
    }

    companion object {
        const val bountyTickFreq = 20L
        const val boardTickFreq = 20L
    }

}