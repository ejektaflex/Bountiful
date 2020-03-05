package ejektaflex.bountiful

import ejektaflex.bountiful.api.IBountifulAPI
import ejektaflex.bountiful.data.bounty.enums.BountyRarity
import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.data.structure.Decree
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


    override fun toBountyData(stack: ItemStack): BountyData {
        return BountyData.from(stack)
    }

    override fun dataToStack(data: BountyData): ItemStack {
        // TODO BountifulAPIImpl::dataToStack
        //return ItemStack(ContentRegistry.bounty).apply { this.tag = data.serializeNBT() }
        return ItemStack.EMPTY
    }

    override fun createBountyData(worldIn: World, rarity: BountyRarity, decrees: List<Decree>): BountyData? {
        return BountyCreator.create(rarity, decrees)
    }

}