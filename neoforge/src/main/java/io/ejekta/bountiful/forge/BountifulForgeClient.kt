package io.ejekta.bountiful.forge

import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.client.BoardScreen
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.item.ItemGroups
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.ConfigScreenHandler
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent
import net.neoforged.neoforge.client.event.ScreenEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent

object BountifulForgeClient {
    @SubscribeEvent
    @JvmStatic
    fun initClient(evt: FMLClientSetupEvent) {
        HandledScreens.register(BountifulContent.BOARD_SCREEN_HANDLER, ::BoardScreen)
        // Register config screen
        if (Bountybridge.isModLoaded("cloth_config")) {
            evt.enqueueWork {
                ModLoadingContext.get().registerExtensionPoint( ConfigScreenHandler.ConfigScreenFactory::class.java) {
                    ConfigScreenHandler.ConfigScreenFactory { c, s -> BountifulIO.configData.buildScreen() }
                }
            }
        }
        // ItemProperties data structures are not thread safe
        evt.enqueueWork {
            Bountybridge.registerItemDynamicTextures()
        }
    }

    @SubscribeEvent
    @JvmStatic
    fun onItemGroups(evt: BuildCreativeModeTabContentsEvent) {
        if (evt.tabKey == ItemGroups.FUNCTIONAL) {
            evt.add { BountifulContent.BOARD_ITEM }
            evt.add { BountifulContent.DECREE_ITEM }
        }
    }

}