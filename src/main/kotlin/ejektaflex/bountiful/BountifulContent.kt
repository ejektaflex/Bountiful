package ejektaflex.bountiful

import ejektaflex.bountiful.block.BlockBountyBoard
import ejektaflex.bountiful.block.BoardTileEntity
import ejektaflex.bountiful.gui.BoardContainer
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.item.ItemDecree
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.*
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.common.extensions.IForgeContainerType
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.ObjectHolder
import thedarkcolour.kotlinforforge.forge.KDeferredRegister

object BountifulContent {

    object Blocks {

        val BlockRegistry = KDeferredRegister(ForgeRegistries.BLOCKS, BountifulMod.MODID)
        val TileEntityRegistry = KDeferredRegister(ForgeRegistries.TILE_ENTITIES, BountifulMod.MODID)

        val BOUNTYBOARD by BlockRegistry.registerObject("bountyboard") {
            BlockBountyBoard()
        }


        //@ObjectHolder(BountifulMod.MODID + ":bounty-te")
        //var BOUNTYTILEENTITY: TileEntityType<BoardTileEntity> = TileEntityType(::BoardTileEntity, setOf(BOUNTYBOARD), null)
        val BOUNTYTILEENTITY by TileEntityRegistry.registerObject("bounty-te") {
            TileEntityType(::BoardTileEntity, setOf(BOUNTYBOARD), null)
        }
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
        val ContainerRegistry = KDeferredRegister(ForgeRegistries.CONTAINERS, BountifulMod.MODID)
        val BOARDCONTAINER by ContainerRegistry.registerObject("bountyboard") {
            IForgeContainerType.create(::BoardContainer)
        }
    }

    object BountifulGroup : ItemGroup(BountifulMod.MODID) {
        override fun createIcon() = ItemStack(Items.DECREE)
    }

}