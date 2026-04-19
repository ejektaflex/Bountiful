package io.ejekta.bountiful.forge

import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.client.AnalyzerScreen
import io.ejekta.bountiful.client.BoardScreen
import io.ejekta.bountiful.client.EditorScreen
import io.ejekta.bountiful.config.BountifulConfigScreen
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.gui.AnalyzerScreenHandler
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.bountiful.content.gui.EditorScreenHandler
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.MenuType
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent


object BountifulForgeClient {
    @SubscribeEvent
    @JvmStatic
    fun initClient(evt: FMLClientSetupEvent) {
        // Register config screen
        if (Bountybridge.isModLoaded("cloth_config")) {
            evt.enqueueWork {
                ModLoadingContext.get().registerExtensionPoint(
                    IConfigScreenFactory::class.java,
                    { IConfigScreenFactory { c, s -> BountifulConfigScreen.buildScreen() } }
                )
            }
        }
        // ItemProperties data structures are not thread safe
        evt.enqueueWork {
            Bountybridge.registerItemDynamicTextures()
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onRegisterClientComponents(event: RegisterMenuScreensEvent) {
        println("REGISTERING MENU SCREENS BO")
        event.register(
            BountifulContent.BOARD_SCREEN_HANDLER,
            MenuScreens.ScreenConstructor(::BoardScreen)
        )
        event.register(
            BountifulContent.ANALYZER_SCREEN_HANDLER,
            MenuScreens.ScreenConstructor(::AnalyzerScreen)
        )
        event.register(
            BountifulContent.EDITOR_SCREEN_HANDLER,
            MenuScreens.ScreenConstructor(::EditorScreen)
        )
    }

    @SubscribeEvent
    @JvmStatic
    fun onItemGroups(evt: BuildCreativeModeTabContentsEvent) {
        val items = Bountybridge.getItemGroups()[evt.tabKey]
        items?.let {
            for (item in items) {
                evt.accept(item)
            }
        }
    }

}
