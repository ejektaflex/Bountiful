package ejektaflex.bountiful

import ejektaflex.bountiful.api.IBountifulAPI
import ejektaflex.bountiful.api.data.IBountyData
import ejektaflex.bountiful.api.data.IDecree
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import ejektaflex.bountiful.data.BountyData
import ejektaflex.bountiful.logic.BountyCreator
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object BountifulAPIImpl : IBountifulAPI {


    override fun getBountiesAt(worldIn: World, pos: BlockPos): List<ItemStack>? {
        // Todo BountifulAPIImpl::getBountiesAt
        //return getBountyBoard(worldIn, pos)?.inventory?.handler?.stacks
        return null
    }


    override fun toBountyData(stack: ItemStack): IBountyData {
        return BountyData.from(stack)
    }

    override fun dataToStack(data: IBountyData): ItemStack {
        // TODO BountifulAPIImpl::dataToStack
        //return ItemStack(ContentRegistry.bounty).apply { this.tag = data.serializeNBT() }
        return ItemStack.EMPTY
    }

    override fun createBountyData(worldIn: World, rarity: EnumBountyRarity, decrees: List<IDecree>): BountyData? {
        return BountyCreator.create(rarity, decrees)
    }

}