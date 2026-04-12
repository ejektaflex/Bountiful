package io.ejekta.bountiful.client

import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.content.BountifulContent
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.gui.screens.MenuScreens

class BountifulClient : ClientModInitializer {

    override fun onInitializeClient() {
        Bountybridge.registerItemDynamicTextures()
        Bountybridge.registerClientMessages()
        MenuScreens.register(BountifulContent.BOARD_SCREEN_HANDLER, ::BoardScreen)
        MenuScreens.register(BountifulContent.ANALYZER_SCREEN_HANDLER, ::AnalyzerScreen)
    }

}
