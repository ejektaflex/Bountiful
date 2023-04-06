package io.ejekta.bountiful

import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.BountifulIO.doContentReload
import io.ejekta.bountiful.content.BountifulCommands
import io.ejekta.kambrik.Kambrik
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import thedarkcolour.kotlinforforge.forge.runForDist

@Mod("bountiful")
object BountifulModForge {
    init {
        Bountybridge.registerServerMessages()
        Bountybridge.registerClientMessages()
        FORGE_BUS.addListener(this::registerCommands)
        FORGE_BUS.addListener(this::onGameReload)
        FORGE_BUS.addListener(this::onEntityKilled)
        FORGE_BUS.addListener(this::onServerStarting)

        MOD_CONTEXT.getKEventBus().register(Bountybridge)

        runForDist(
            clientTarget = {
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

    private fun onServerStarting(evt: ServerStartingEvent) {
        listOf("plains", "savanna", "snowy", "taiga", "desert").forEach { villageType ->
            Bountiful.LOGGER.info("Registering Bounty Board Jigsaw Piece for Village Type: $villageType")
            Kambrik.Structure.addToStructurePool(
                evt.server,
                Identifier("bountiful:village/common/bounty_gazebo"),
                Identifier("minecraft:village/$villageType/houses"),
                BountifulIO.configData.boardGenFrequency
            )
        }
    }

    private fun onGameReload(evt: AddReloadListenerEvent) {
        evt.addListener(SynchronousResourceReloader { manager ->
            doContentReload(manager)
        })
    }

    private fun registerCommands(evt: RegisterCommandsEvent) {
        BountifulCommands.register(evt.dispatcher, evt.buildContext, evt.commandSelection)
    }

}