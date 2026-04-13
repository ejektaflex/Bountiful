package io.ejekta.bountiful

import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.BountifulReloadListener
import io.ejekta.bountiful.content.BountifulCommands
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.registration.KambrikRegistrar
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.PackType

class BountifulModFabric : ModInitializer {

    init {

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(BountifulReloadListener)
        val ourContainer = FabricLoader.getInstance().getModContainer(Bountiful.ID).get()

        listOf(
            "campanion",
            "charm",
            "croptopia",
            "gofish",
            "techreborn",
            "villager-hats",
            "xtraarrows",
            "numismatic-overhaul"
        ).forEach {
            if (FabricLoader.getInstance().isModLoaded(it)) {
                val modContainer = FabricLoader.getInstance().getModContainer(it).get()
                ResourceManagerHelper.registerBuiltinResourcePack(
                    Bountiful.id("compat-$it"),
                    ourContainer,
                    Component.translatable("bountiful.compat.resource_pack", ourContainer.metadata.name, modContainer.metadata.name),
                    ResourcePackActivationType.DEFAULT_ENABLED
                )
            }
        }

    }

    override fun onInitialize() {
        Bountiful.LOGGER.info("Bountiful Common init")
        BountifulIO.loadConfig()

        KambrikRegistrar.doRegistrationsFor(BountifulContent)

        Bountybridge.registerServerMessages()
        if (FabricLoader.getInstance().environmentType == net.fabricmc.api.EnvType.SERVER) {
            Bountybridge.registerClientMessages()
        }

        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback(BountifulCommands::register))

        Bountybridge.registerCompostables()

        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleEvents.ServerStarting { server ->
            Bountybridge.registerJigsawPieces(server)
        })

        // Experimental.. (Fabric Only)
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { server ->
            BountifulIO.configData.chaosMode?.inject(server)
        })

        // Increment entity bounties for all players within 12 blocks of the player and all players within 12 blocks of the mob
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(ServerEntityCombatEvents.AfterKilledOtherEntity { world, entity, killedEntity, damageSource ->
            Bountybridge.handleEntityKills(world, entity, killedEntity)
        })

        // TODO 26.1.2 trades are data-driven; reintroduce bounty trades with the new system.

        for ((group, items) in Bountybridge.getItemGroups()) {
            CreativeModeTabEvents.modifyOutputEvent(group).register(CreativeModeTabEvents.ModifyOutput { output ->
                for (item in items) {
                    output.accept(item())
                }
            })
        }

        Bountybridge.registerCriterionStuff()
    }
}
