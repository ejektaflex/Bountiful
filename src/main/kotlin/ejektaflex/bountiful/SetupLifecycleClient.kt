package ejektaflex.bountiful

import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.ext.getUnsortedList
import ejektaflex.bountiful.ext.toData
import ejektaflex.bountiful.gui.BoardScreen
import net.minecraft.client.gui.ScreenManager
import net.minecraft.item.ItemModelsProperties
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DeferredWorkQueue
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(value = [Dist.CLIENT], bus = Mod.EventBusSubscriber.Bus.MOD)
object SetupLifecycleClient {
    @SubscribeEvent
    fun onClientInit(event: FMLClientSetupEvent) {
        println("Client initting now")

        DeferredWorkQueue.runLater {
            ScreenManager.registerFactory(BountifulContent.BOARDCONTAINER) { container, inv, textComponent ->
                BoardScreen(container, inv, textComponent)
            }
            println("BoRegistered Board Screen factory")
        }



        // Item Property Overrides

        ItemModelsProperties.registerProperty(BountifulContent.BOUNTY, ResourceLocation("bountiful", "rarity")) { stack, _, _ ->
            if (stack.hasTag()) {
                stack.toData(::BountyData).rarity * 0.1f
            } else {
                0.0f
            }
        }


        ItemModelsProperties.registerProperty(BountifulContent.DECREE, ResourceLocation("bountiful", "decreestatus")) { stack, world, entity ->
            if (stack.hasTag() && stack.tag!!.getUnsortedList("ids").isNotEmpty()) {
                1f
            } else {
                0f
            }
        }

    }
}