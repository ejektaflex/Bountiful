package ejektaflex.bountiful

import ejektaflex.bountiful.block.BlockBountyBoard
import ejektaflex.bountiful.block.BoardTileEntity
import ejektaflex.bountiful.gui.BoardContainer
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.item.ItemDecree
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.common.extensions.IForgeContainerType
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.KDeferredRegister

object BountifulContent {

    val ContainerRegistry = KDeferredRegister(ForgeRegistries.CONTAINERS, BountifulMod.MODID)
    val BlockRegistry = KDeferredRegister(ForgeRegistries.BLOCKS, BountifulMod.MODID)
    val TileEntityRegistry = KDeferredRegister(ForgeRegistries.TILE_ENTITIES, BountifulMod.MODID)
    val ItemRegistry = KDeferredRegister(ForgeRegistries.ITEMS, BountifulMod.MODID)
    val FeatureRegistry = KDeferredRegister(ForgeRegistries.FEATURES, BountifulMod.MODID)

    val BOUNTYBOARD by BlockRegistry.registerObject("bountyboard") {
        BlockBountyBoard()
    }

    val BOUNTYTILEENTITY by TileEntityRegistry.registerObject("bounty-te") {
        TileEntityType(::BoardTileEntity, setOf(BOUNTYBOARD), null)
    }

    val BOUNTY by ItemRegistry.registerObject("bounty") {
        ItemBounty()
    }

    val BOUNTYBOARDITEM by ItemRegistry.registerObject("bountyboard") {
        BlockItem(BOUNTYBOARD, Item.Properties().group(ItemGroup.MISC))
    }

    val DECREE by ItemRegistry.registerObject("decree", ::ItemDecree)

    val BOARDCONTAINER: ContainerType<BoardContainer> by ContainerRegistry.registerObject("bountyboard") {
        IForgeContainerType.create(::BoardContainer)
    }

}