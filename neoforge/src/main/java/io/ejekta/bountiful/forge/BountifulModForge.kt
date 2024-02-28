package io.ejekta.bountiful.forge

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.BountifulIO.doContentReload
import io.ejekta.bountiful.content.BountifulCommands
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import net.minecraft.block.ComposterBlock
import net.minecraft.block.MapColor
import net.minecraft.entity.passive.WanderingTraderEntity
import net.minecraft.item.Item
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.world.WanderingTraderManager
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.event.AddReloadListenerEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.event.village.VillagerTradesEvent
import net.neoforged.neoforge.event.village.WandererTradesEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_CONTEXT
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist
import java.util.function.Supplier


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
        FORGE_BUS.addListener(this::changeTrades)

        val content = BountifulContent

        MOD_CONTEXT.getKEventBus().register(Companion)

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
        Bountybridge.registerJigsawPieces(evt.server)
    }

    private fun onGameReload(evt: AddReloadListenerEvent) {
        evt.addListener(SynchronousResourceReloader { manager ->
            doContentReload(manager)
        })
    }

    private fun registerCommands(evt: RegisterCommandsEvent) {
        BountifulCommands.register(evt.dispatcher, evt.buildContext, evt.commandSelection)
    }

    private fun changeTrades(evt: WandererTradesEvent) {
        Bountybridge.modifyTradeList(evt.rareTrades)
    }

    companion object {
        @JvmStatic
        @SubscribeEvent
        fun registerRegistryContent(evt: RegisterEvent) {
            KambrikRegistrar[BountifulContent].content.forEach { entry ->
                evt.register(entry.registry.key as RegistryKey<out Registry<Any>>) {
                    it.register(Identifier(BountifulContent.getId(), entry.itemId), entry.item.value!!)
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