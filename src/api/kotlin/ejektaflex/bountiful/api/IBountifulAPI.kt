package ejektaflex.bountiful.api

import ejektaflex.bountiful.api.block.ITileEntityBountyBoard
import ejektaflex.bountiful.api.config.IBountifulConfig
import ejektaflex.bountiful.api.data.IBountyData
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import ejektaflex.bountiful.api.logic.pickable.PickableEntry
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack
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
     * Retrieves the tile entity of a bounty board
     */
    fun getBountyBoard(worldIn: World, pos: BlockPos): ITileEntityBountyBoard?

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
    val config: IBountifulConfig

    /**
     * Creates data for a new bounty.
     */
    fun createBountyData(rarity: EnumBountyRarity?): IBountyData?

}