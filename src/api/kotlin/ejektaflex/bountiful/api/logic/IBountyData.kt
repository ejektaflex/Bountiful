package ejektaflex.bountiful.api.logic

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

interface IBountyData : INBTSerializable<NBTTagCompound> {

    /**
     * How long a bounty has left on the Bounty Board
     */
    var boardTime: Int

    /**
     * How long the bounty has before it expires.
     */
    var bountyTime: Long

    /**
     * An integer representing the EnumBountyRarity of the bounty
     */
    var rarity: Int

    /**
     * A list of pairs of itemstacks needed to fulfill a bounty, and the amount needed.
     */
    val toGet: MutableList<Pair<ItemStack, Int>>

    /**
     * A list of pairs of itemstacks used as rewards for the bounty, and the amount needed.
     */
    val rewards: MutableList<Pair<ItemStack, Int>>

    /**
     * A long representing the last bountyTime (in world tick bountyTime) that the item's bountyTime was ticked down
     */
    var tickdown: Long


}
