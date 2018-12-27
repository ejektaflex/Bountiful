package ejektaflex.bountiful

import ejektaflex.bountiful.api.IBountifulAPI
import ejektaflex.bountiful.api.config.IBountifulConfig
import ejektaflex.bountiful.api.ext.stacks
import ejektaflex.bountiful.api.item.IItemBounty
import ejektaflex.bountiful.api.logic.IBountyData
import ejektaflex.bountiful.api.logic.pickable.PickableEntry
import ejektaflex.bountiful.api.registry.IValueRegistry
import ejektaflex.bountiful.block.TileEntityBountyBoard
import ejektaflex.bountiful.config.ConfigFile
import ejektaflex.bountiful.registry.BountyRegistry
import ejektaflex.bountiful.registry.RewardRegistry
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object InternalAPI : IBountifulAPI {

    override val bountyRegistry = BountyRegistry
    override val rewardRegistry = RewardRegistry

    override fun getBountiesAt(worldIn: World, pos: BlockPos): List<ItemStack>? {
        return (worldIn.getTileEntity(pos) as? TileEntityBountyBoard)?.inventory?.stacks
    }

    override fun toBountyData(stack: ItemStack): IBountyData {
        if (stack.item is IItemBounty) {
            return (stack.item as IItemBounty).getBountyData(stack)
        } else {
            throw Exception("${stack.displayName} is not an IItemBounty and cannot be converted to bounty data!")
        }
    }

    override fun dataToStack(data: IBountyData): ItemStack {
        return ItemStack(ContentRegistry.bounty).apply { this.tagCompound = data.serializeNBT() }
    }

}