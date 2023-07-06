package io.ejekta.bountiful.forge

import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.client.BoardScreen
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.item.ItemGroups
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

object BountifulForgeClient {
    @SubscribeEvent
    @JvmStatic
    fun initClient(evt: FMLClientSetupEvent) {
        HandledScreens.register(BountifulContent.BOARD_SCREEN_HANDLER, ::BoardScreen)
        // Register config screen
        if (Bountybridge.isModLoaded("cloth_config")) {
            evt.enqueueWork {
                ModLoadingContext.get().registerExtensionPoint( ConfigScreenFactory::class.java) {
                    ConfigScreenFactory { c, s -> BountifulIO.configData.buildScreen() }
                }
            }
        }
        Bountybridge.registerItemDynamicTextures()
    }

    @SubscribeEvent
    @JvmStatic
    fun onItemGroups(evt: BuildCreativeModeTabContentsEvent) {
        if (evt.tabKey == ItemGroups.FUNCTIONAL) {
            evt.accept { BountifulContent.BOARD_ITEM }
            evt.accept { BountifulContent.DECREE_ITEM }
        }
    }

}