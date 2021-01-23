package ejektaflex.bountiful

import ejektaflex.bountiful.block.BlockBountyBoard
import ejektaflex.bountiful.block.BoardTileEntity
import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.ext.getUnsortedList
import ejektaflex.bountiful.ext.toData
import ejektaflex.bountiful.gui.BoardContainer
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.item.ItemDecree
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.*
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.ObjectHolder
import thedarkcolour.kotlinforforge.forge.KDeferredRegister

object BountifulContent {

    object Blocks {

        val BlockRegistry = KDeferredRegister(ForgeRegistries.BLOCKS, BountifulMod.MODID)

        val BOUNTYBOARD by BlockRegistry.registerObject("bountyboard") {
            BlockBountyBoard()
        }

            @ObjectHolder(BountifulMod.MODID + ":bounty-te")
            @JvmStatic
            lateinit var BOUNTYTILEENTITY: TileEntityType<BoardTileEntity>
    }

    object Items {

        val ItemRegistry = KDeferredRegister(ForgeRegistries.ITEMS, BountifulMod.MODID)

        val BOUNTY by ItemRegistry.registerObject("bounty") {
            ItemBounty()
        }

        val BOUNTYBOARD by ItemRegistry.registerObject("bountyboard") {
            BlockItem(Blocks.BOUNTYBOARD, Item.Properties().group(BountifulGroup))
        }


        val DECREE by ItemRegistry.registerObject("decree", ::ItemDecree)
    }

    object Guis {
        @ObjectHolder(BountifulMod.MODID + ":bountyboard")
        @JvmStatic
        lateinit var BOARDCONTAINER: ContainerType<BoardContainer>
    }

    object BountifulGroup : ItemGroup(BountifulMod.MODID) {
        override fun createIcon() = ItemStack(Items.DECREE)
    }

}