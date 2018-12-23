package bountiful

import bountiful.block.BlockBountyBoard
import bountiful.block.BlockTileEntity
import bountiful.item.ItemBounty
import bountiful.logic.BountyCreator
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.client.ForgeClientHandler.registerModels
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraftforge.client.model.ModelLoader






object ContentRegistry {

    val bounty = ItemBounty().apply {
        registryName = ResourceLocation(BountifulInfo.MODID, "bounty")
        unlocalizedName = "bountiful.bounty"
    }

    val bountyBlock = BlockBountyBoard().apply {
            registryName = ResourceLocation(BountifulInfo.MODID, "bountyboard")
            unlocalizedName = "bountiful.bountyboardblock"
    }

    val bountyItemBlock = ItemBlock(bountyBlock).apply {
        registryName = ResourceLocation(BountifulInfo.MODID, "bountyboarditem")
        unlocalizedName = "bountiful.bountyboarditem"
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