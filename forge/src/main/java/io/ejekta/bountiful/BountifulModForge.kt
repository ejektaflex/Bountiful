package io.ejekta.bountiful

import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.config.BountifulIO.doContentReload
import io.ejekta.bountiful.content.BountifulCommands
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.server.world.ServerWorld
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import thedarkcolour.kotlinforforge.forge.runForDist

@Mod("bountiful")
object BountifulModForge {
    init {
        println("Bountiful forge init called")

        Bountybridge.registerMessages()
        FORGE_BUS.addListener(this::registerCommands)
        FORGE_BUS.addListener(this::onGameReload)
        FORGE_BUS.addListener(this::onEntityKilled)

        MOD_CONTEXT.getKEventBus().register(Bountybridge)

        runForDist(
            clientTarget = {
                println("Registering client listeners for Bountiful..")
                MOD_CONTEXT.getKEventBus().register(BountifulForgeClient::class.java)
            },
            serverTarget = {

            }
        )
        Bountybridge.registerCriterionStuff()
    }

    private fun onEntityKilled(evt: LivingDeathEvent) {
        evt.source.attacker?.let { attacker ->
            (evt.entity.world as? ServerWorld)?.let { serverWorld ->
                Bountybridge.handleEntityKills(serverWorld, attacker, evt.entity)
            }
        }
    }

    private fun onGameReload(evt: AddReloadListenerEvent) {
        evt.addListener(SynchronousResourceReloader { manager ->
            doContentReload(manager)
        })
    }

    private fun registerCommands(evt: RegisterCommandsEvent) {
        println("Forge evt bus registering Bountiful commands")
        BountifulCommands.register(evt.dispatcher, evt.buildContext, evt.commandSelection)
    }



}