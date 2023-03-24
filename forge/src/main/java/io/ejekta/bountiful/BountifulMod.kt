package io.ejekta.bountiful

import io.ejekta.bountiful.content.BountifulCommands
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.FORGE_BUS

@Mod("bountiful")
object BountifulMod {
    init {
        println("Bountiful forge init called")

        FORGE_BUS.addListener(this::registerCommands)

    }


    @JvmStatic
    @SubscribeEvent
    fun registerCommands(evt: RegisterCommandsEvent) {
        println("Forge evt bus registering Bountiful commands")
        BountifulCommands.register(evt.dispatcher, evt.buildContext, evt.commandSelection)
    }


}