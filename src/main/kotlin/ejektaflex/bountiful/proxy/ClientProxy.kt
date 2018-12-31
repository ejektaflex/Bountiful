package ejektaflex.bountiful.proxy

import ejektaflex.bountiful.BountifulInfo
import ejektaflex.bountiful.ContentRegistry
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class ClientProxy : CommonProxy() {

    private fun registerItemRenderer(item: Item, meta: Int, id: String) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(BountifulInfo.MODID + ":" + id, "inventory"))
    }

    @SubscribeEvent
    fun registerModels(event: ModelRegistryEvent) {
        ContentRegistry.items.forEach {
            registerItemRenderer(it, 0, it.registryName!!.resourcePath)
        }
    }

}