package io.ejekta.bountiful.forge

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.config.BountifulIO.doContentReload
import io.ejekta.bountiful.content.BountifulCommands
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.registration.KambrikRegistrar
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.event.AddReloadListenerEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_CONTEXT
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist
import java.util.concurrent.CompletableFuture


@Mod("bountiful")
class BountifulModForge {
    init {

        Bountiful.LOGGER.info("Registering Network Messages..")

        Bountybridge.registerServerMessages()
        Bountybridge.registerClientMessages()

        FORGE_BUS.addListener(this::registerCommands)
        FORGE_BUS.addListener(this::onGameReload)
        FORGE_BUS.addListener(this::onEntityKilled)
        FORGE_BUS.addListener(this::onServerStarting)

        val content = BountifulContent // trigger init

        MOD_CONTEXT.getKEventBus().register(Companion)

        runForDist(
            clientTarget = {
                MOD_CONTEXT.getKEventBus().register(BountifulForgeClient::class.java)
            },
            serverTarget = {
                // no-op, nothing specific here
            }
        )
        Bountybridge.registerCriterionStuff()
    }

    private fun onEntityKilled(evt: LivingDeathEvent) {
        evt.source.entity?.let { attacker ->
            (evt.entity.level() as? ServerLevel)?.let { serverWorld ->
                Bountybridge.handleEntityKills(serverWorld, attacker, evt.entity)
            }
        }
    }

    private fun onServerStarting(evt: ServerAboutToStartEvent) {
        Bountybridge.registerJigsawPieces(evt.server)
    }

    private fun onGameReload(evt: AddReloadListenerEvent) {
        evt.addListener(PreparableReloadListener { prepBarrier, resourceManager, pfa, pfb, ea, eb ->
            return@PreparableReloadListener prepBarrier.wait(
                CompletableFuture.supplyAsync({
                    doContentReload(resourceManager)
                    null
                }, eb).get()
            )
        })
    }

    private fun registerCommands(evt: RegisterCommandsEvent) {
        BountifulCommands.register(evt.dispatcher, evt.buildContext, evt.commandSelection)
    }

    // TODO 26.1.2 trades are data-driven; reintroduce bounty trades with the new system.

    companion object {
        @JvmStatic
        @SubscribeEvent
        fun registerRegistryContent(evt: RegisterEvent) {
            KambrikRegistrar[BountifulContent].content.forEach { entry ->
                evt.register(entry.registry.key() as ResourceKey<out Registry<Any>>) {
                    it.register(ResourceLocation.fromNamespaceAndPath(BountifulContent.getId(), entry.itemId), entry.item.value!!)
                }
            }
        }

        @JvmStatic
        @SubscribeEvent
        private fun commonSetup(evt: FMLCommonSetupEvent) {
            evt.enqueueWork {
                Bountybridge.registerCompostables()
            }
        }
    }

}
