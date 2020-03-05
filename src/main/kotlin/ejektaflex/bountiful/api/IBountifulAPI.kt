package ejektaflex.bountiful.api

import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.data.structure.Decree
import ejektaflex.bountiful.data.bounty.enums.BountyRarity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface IBountifulAPI {

    /**
     * Retrieves the tile entity of a bounty board
     */
    //fun getBountyBoard(worldIn: World, pos: BlockPos): ITileEntityBountyBoard?

    /**
     * Returns a list of bounties at a given bounty board
     */
    fun getBountiesAt(worldIn: World, pos: BlockPos): List<ItemStack>?

    /**
     * Converts an IItemBounty ItemStack to bounty data
     */
    fun toBountyData(stack: ItemStack): BountyData

    /**
     * Converts bounty data to a bounty itemstack
     */
    fun dataToStack(data: BountyData): ItemStack

    /**
     * Creates data for a new bounty.
     */
    fun createBountyData(worldIn: World, rarity: BountyRarity, decrees: List<Decree>): BountyData?

}