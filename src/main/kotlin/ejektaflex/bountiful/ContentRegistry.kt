package ejektaflex.bountiful

import ejektaflex.bountiful.block.BlockBountyBoard
import ejektaflex.bountiful.block.BlockTileEntity
import ejektaflex.bountiful.item.ItemBounty
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry


object ContentRegistry {

    val bounty = ItemBounty().apply {
        registryName = ResourceLocation(BountifulInfo.MODID, "bounty")
        translationKey = "bountiful.bounty"
        setMaxStackSize(1)
    }

    val bountyBlock = BlockBountyBoard().apply {
            registryName = ResourceLocation(BountifulInfo.MODID, "bountyboard")
            translationKey = "bountiful.bountyboardblock"
    }

    val bountyItemBlock = ItemBlock(bountyBlock).apply {
        registryName = ResourceLocation(BountifulInfo.MODID, "bountyboarditem")
        translationKey = "bountiful.bountyboarditem"
    }

    val blocks = listOf(
            bountyBlock
    )

    val items = listOf(
            bounty,
            bountyItemBlock
    )

    @SubscribeEvent
    fun registerBlocks(event: RegistryEvent.Register<Block>) {



        event.registry.registerAll(*blocks.toTypedArray())

        blocks.forEach {
            if (it is BlockTileEntity<*>) {
                GameRegistry.registerTileEntity(it.tileEntityClass, it.registryName.toString())
            }
        }
    }

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        event.registry.registerAll(*items.toTypedArray())
    }

}