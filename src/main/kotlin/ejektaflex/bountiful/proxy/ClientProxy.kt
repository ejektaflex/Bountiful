package ejektaflex.bountiful.proxy

import ejektaflex.bountiful.BountifulInfo
import ejektaflex.bountiful.ContentRegistry
import ejektaflex.bountiful.api.BountifulAPI
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import ejektaflex.bountiful.item.ItemBounty
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.I18n
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class ClientProxy : CommonProxy() {

    @SideOnly(Side.CLIENT)
    private class BountyItemDefinition(val id: String) : ItemMeshDefinition {
        override fun getModelLocation(stack: ItemStack): ModelResourceLocation {
            val locid = BountifulInfo.MODID + ":" + id
            return try {
                val data = BountifulAPI.toBountyData(stack)
                val rarity = EnumBountyRarity.getRarityFromInt(data.rarity)
                ModelResourceLocation("$locid-${rarity.name.toLowerCase()}", "inventory")
            } catch (e: Exception) {
                ModelResourceLocation("$locid-${EnumBountyRarity.Common.name.toLowerCase()}", "inventory")
            }
        }
    }

    private fun registerItemRenderer(item: Item, meta: Int, id: String) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(BountifulInfo.MODID + ":" + id, "inventory"))
    }

    private fun registerBountyRenderer(item: Item, meta: Int, id: String) {
        for (rarity in EnumBountyRarity.values().map { it.name.toLowerCase() }) {
            ModelBakery.registerItemVariants(item, ResourceLocation(BountifulInfo.MODID + ":" + id + "-$rarity"))
        }
        ModelLoader.setCustomMeshDefinition(item, BountyItemDefinition(id))
    }

    @SubscribeEvent
    fun registerModels(event: ModelRegistryEvent) {
        ContentRegistry.items.forEach {
            if (it is ItemBounty) {
                registerBountyRenderer(it, 0, it.registryName!!.path)
            } else {
                registerItemRenderer(it, 0, it.registryName!!.path)
            }
        }
    }

}