package io.ejekta.bountiful

import io.ejekta.BountifulForgeClient
import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.config.BountifulIO.doContentReload
import io.ejekta.bountiful.content.BountifulCommands
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import thedarkcolour.kotlinforforge.forge.runForDist

@Mod("bountiful")
object BountifulModForge {
    init {
        println("Bountiful forge init called")

        FORGE_BUS.addListener(this::registerCommands)
        FORGE_BUS.addListener(this::onGameReload)

        MOD_CONTEXT.getKEventBus().register(Bountybridge)

        runForDist(
            clientTarget = {
                println("Registering client listeners for Bountiful..")
                MOD_CONTEXT.getKEventBus().register(BountifulForgeClient::class.java)
            },
            serverTarget = {

            }
        )

    }

    val BountyDataReloader = SynchronousResourceReloader { manager ->
        doContentReload(manager)
    }

    @JvmStatic
    @SubscribeEvent
    fun onGameReload(evt: AddReloadListenerEvent) {
        evt.addListener(BountyDataReloader)
    }

    @JvmStatic
    @SubscribeEvent
    fun registerCommands(evt: RegisterCommandsEvent) {
        println("Forge evt bus registering Bountiful commands")
        BountifulCommands.register(evt.dispatcher, evt.buildContext, evt.commandSelection)
    }



}