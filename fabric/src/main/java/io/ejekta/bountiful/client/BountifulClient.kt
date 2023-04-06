package io.ejekta.bountiful.client

import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.content.BountifulContent
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry

class BountifulClient : ClientModInitializer {

    override fun onInitializeClient() {
        Bountybridge.registerItemDynamicTextures()
        //Bountybridge.registerClientMessages()
        ScreenRegistry.register(BountifulContent.BOARD_SCREEN_HANDLER, ::BoardScreen)
    }

}