package io.ejekta.bountiful

import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.content.BountifulCommands
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

@Mod("bountiful")
object BountifulModForge {
    init {
        println("Bountiful forge init called")

        FORGE_BUS.addListener(this::registerCommands)

        MOD_CONTEXT.getKEventBus().register(Bountybridge)

        Kambrik.Logger.debug("Using Kambrik logger from Bountiful, oops!")

    }


    @JvmStatic
    @SubscribeEvent
    fun registerCommands(evt: RegisterCommandsEvent) {
        println("Forge evt bus registering Bountiful commands")
        BountifulCommands.register(evt.dispatcher, evt.buildContext, evt.commandSelection)
    }



}