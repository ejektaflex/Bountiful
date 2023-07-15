package io.ejekta.bountiful

import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.BountifulReloadListener
import io.ejekta.bountiful.content.BountifulCommands
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.ItemGroups
import net.minecraft.resource.ResourceType
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class BountifulModFabric : ModInitializer {

    init {

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(BountifulReloadListener)

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
            val ourContainer = FabricLoader.getInstance().getModContainer(Bountiful.ID).get()
            if (FabricLoader.getInstance().isModLoaded(it)) {
                val modContainer = FabricLoader.getInstance().getModContainer(it).get()
                ResourceManagerHelper.registerBuiltinResourcePack(
                    Identifier(Bountiful.ID, "compat-$it"),
                    ourContainer,
                    Text.literal("${ourContainer.metadata.name} - ${modContainer.metadata.name} Compat"),
                    ResourcePackActivationType.DEFAULT_ENABLED
                )
            }
        }

    }

    override fun onInitialize() {
        Bountiful.LOGGER.info("Common init")
        BountifulIO.loadConfig()
        KambrikRegistrar.doRegistrationsFor(Bountiful.ID)

        Bountybridge.registerServerMessages()
        Bountybridge.registerClientMessages()

        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback(BountifulCommands::register))

        CompostingChanceRegistry.INSTANCE.add({ BountifulContent.BOUNTY_ITEM }, 0.5f)
        CompostingChanceRegistry.INSTANCE.add({ BountifulContent.DECREE_ITEM }, 0.85f)

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register { e ->
            e.add(BountifulContent.DECREE_ITEM)
            e.add(BountifulContent.BOARD_ITEM)
        }

        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleEvents.ServerStarting { server ->
            listOf("plains", "savanna", "snowy", "taiga", "desert").forEach { villageType ->
                Bountiful.LOGGER.info("Registering Bounty Board Jigsaw Piece for Village Type: $villageType")
                Kambrik.Structure.addToStructurePool(
                    server,
                    Identifier("bountiful:village/common/bounty_gazebo"),
                    Identifier("minecraft:village/$villageType/houses"),
                    Identifier("bountiful:$villageType"),
                    BountifulIO.configData.boardGenFrequency
                )
            }
        })

        // Increment entity bounties for all players within 12 blocks of the player and all players within 12 blocks of the mob
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(ServerEntityCombatEvents.AfterKilledOtherEntity { world, entity, killedEntity ->
            Bountybridge.handleEntityKills(world, entity, killedEntity)
        })

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(ItemGroupEvents.ModifyEntries {
            it.add(BountifulContent.BOARD_ITEM)
            it.add(BountifulContent.DECREE_ITEM)
        })

        Bountybridge.registerCriterionStuff()
    }
}