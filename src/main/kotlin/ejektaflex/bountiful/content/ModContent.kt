package ejektaflex.bountiful.content

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.block.BlockBountyBoard
import ejektaflex.bountiful.block.BountyTE
import ejektaflex.bountiful.item.ItemBounty
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.registries.ObjectHolder

object ModContent {

    object Blocks {

        val BOUNTYBOARD = BlockBountyBoard().setRegistryName("bountyboard")

        @ObjectHolder(BountifulMod.MODID + ":bounty-te")
        @JvmStatic lateinit var BOUNTYTILEENTITY: TileEntityType<BountyTE>

    }

    object Items {
        val BOUNTY = ItemBounty().setRegistryName("bounty")
        val BOUNTYBOARD = BlockItem(Blocks.BOUNTYBOARD, Item.Properties()).setRegistryName("bountyboard")
    }

}