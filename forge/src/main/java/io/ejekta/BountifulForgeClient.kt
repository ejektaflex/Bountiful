package io.ejekta

import io.ejekta.bountiful.client.BoardScreen
import io.ejekta.bountiful.content.BountifulContent
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

object BountifulForgeClient {
    @SubscribeEvent
    @JvmStatic
    fun initClient(evt: FMLClientSetupEvent) {
        println("Bountiful setting up forge client init!")
        HandledScreens.register(BountifulContent.BOARD_SCREEN_HANDLER, ::BoardScreen)
    }

}