package ejektaflex.bountiful

import ejektaflex.bountiful.block.BlockBountyBoard
import ejektaflex.bountiful.block.BoardBlockEntity
import ejektaflex.bountiful.gui.BoardMenu
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.item.ItemDecree
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject

object BountifulContent {

    //val ContainerRegistry = KDeferredRegister(ForgeRegistries.CONTAINERS, BountifulMod.MODID)
    val BlockRegistry = DeferredRegister.create(ForgeRegistries.BLOCKS, BountifulMod.MODID)
    val BlockEntityRegistry = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BountifulMod.MODID)
    val ItemRegistry = DeferredRegister.create(ForgeRegistries.ITEMS, BountifulMod.MODID)
    val FeatureRegistry = DeferredRegister.create(ForgeRegistries.FEATURES, BountifulMod.MODID)

    val BOUNTYBOARD by BlockRegistry.registerObject("bountyboard") {
        BlockBountyBoard()
    }

    val BOUNTYTILEENTITY by BlockEntityRegistry.registerObject("bounty-be") {
        BlockEntityType(::BoardBlockEntity, setOf(BOUNTYBOARD), null)
    }

    val BOUNTY by ItemRegistry.registerObject("bounty") {
        ItemBounty()
    }

    val BOUNTYBOARDITEM by ItemRegistry.registerObject("bountyboard") {
        BlockItem(BOUNTYBOARD, Item.Properties())
    }

    val DECREE by ItemRegistry.registerObject("decree", ::ItemDecree)

    val BOARDCONTAINER: ContainerType<BoardMenu> by ContainerRegistry.registerObject("bountyboard") {
        IForgeContainerType.create(::BoardContainer)
    }

}