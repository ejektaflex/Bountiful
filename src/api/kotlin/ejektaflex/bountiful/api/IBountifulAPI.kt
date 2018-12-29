package ejektaflex.bountiful.api

import ejektaflex.bountiful.api.config.IBountifulConfig
import ejektaflex.bountiful.api.logic.IBountyData
import ejektaflex.bountiful.api.logic.pickable.PickableEntry
import ejektaflex.bountiful.api.logic.pickable.PickedEntryStack
import ejektaflex.bountiful.api.registry.IValueRegistry
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface IBountifulAPI {
    /**
     * The registry containing all possible bounty items
     */
    val bountyRegistry: IValueRegistry<PickableEntry>

    /**
     * The registry containing all possible bounty reward items
     */
    val rewardRegistry: IValueRegistry<PickedEntryStack>

    /**
     * Returns a list of bounties at a given bounty board
     */
    fun getBountiesAt(worldIn: World, pos: BlockPos): List<ItemStack>?

    /**
     * Converts an IItemBounty ItemStack to bounty data
     */
    fun toBountyData(stack: ItemStack): IBountyData

    /**
     * Converts bounty data to a bounty itemstack
     */
    fun dataToStack(data: IBountyData): ItemStack

    /**
     * Retrieves config information about Bountiful
     */
    fun getConfig(): IBountifulConfig

}