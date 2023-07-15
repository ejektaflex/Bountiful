package io.ejekta.bountiful.forge

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.BountifulIO.doContentReload
import io.ejekta.bountiful.content.BountifulCommands
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import thedarkcolour.kotlinforforge.forge.runForDist

@Mod("bountiful")
class BountifulModForge {
    init {

        Bountybridge.registerServerMessages()
        Bountybridge.registerClientMessages()

        FORGE_BUS.addListener(this::registerCommands)
        FORGE_BUS.addListener(this::onGameReload)
        FORGE_BUS.addListener(this::onEntityKilled)
        FORGE_BUS.addListener(this::onServerStarting)

        MOD_CONTEXT.getKEventBus().register(this)

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
                Identifier("bountiful:$villageType"),
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

    @SubscribeEvent
    fun registerRegistryContent(evt: RegisterEvent) {
        KambrikRegistrar[BountifulContent].content.forEach { entry ->
            @Suppress("UNCHECKED_CAST")
            evt.register(entry.registry.key as RegistryKey<out Registry<Any>>, Identifier(BountifulContent.getId(), entry.itemId)) { entry.item }
        }
    }

}