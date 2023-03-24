package io.ejekta.bountiful

import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.BountifulReloadListener
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.messages.*
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.internal.registration.KambrikRegistrar
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.ItemGroups
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
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
            "xtraarrows"
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
                    BountifulIO.configData.boardGenFrequency
                )

            }
        })

        Kambrik.Message.registerServerMessage(
            SelectBounty.serializer(),
            SelectBounty::class,
            Bountiful.id("select_bounty")
        )

        // Increment entity bounties for all players within 12 blocks of the player and all players within 12 blocks of the mob
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(ServerEntityCombatEvents.AfterKilledOtherEntity { world, entity, killedEntity ->
            if (entity is ServerPlayerEntity) {
                val playersInAction = world.getPlayers { it.distanceTo(entity) < 12f } + world.getPlayers { it.distanceTo(killedEntity) < 12f } + entity
                playersInAction.toSet().forEach {
                    BountyTypeRegistry.ENTITY.incrementEntityBounties(it, killedEntity)
                }
            }
        })

        // TODO reimplement Criterion for Kambrik
//        // Update Criterion bounties
//        Kambrik.Criterion.subscribe { player, criterion, predicate ->
//            if (criterion !is TickCriterion && criterion !is EnterBlockCriterion) {
//                player.iterateBountyStacks {
//                    val data = BountyData[this]
//
//                    val triggerObjs = data.objectives.filter { it.critConditions != null }.takeIf { it.isNotEmpty() }
//                        ?: emptyList()
//
//                    for (obj in triggerObjs) {
//
//                        val result = Kambrik.Criterion.testAgainst(
//                            criterion,
//                            Kambrik.Criterion.createCriterionConditionsFromJson(
//                                buildJsonObject {
//                                    put("trigger", obj.content)
//                                    put("conditions", obj.critConditions ?: buildJsonObject {  })
//                                }
//                            ) ?: continue,
//                            predicate
//                        )
//
//                        if (result) {
//                            obj.current += 1
//                            UpdateBountyCriteriaObjective(
//                                player.inventory.indexOf(this),
//                                data.objectives.indexOf(obj)
//                            ).sendToClient(player)
//
//                            if (!isClientSide()) {
//                                BountyData[this] = data // update server with new data
//                            }
//                        }
//                    }
//
//                    data.checkForCompletionAndAlert(player, this)
//
//                }
//            }
//        }

        Kambrik.Message.registerClientMessage(
            ClipboardCopy.serializer(),
            ClipboardCopy::class,
            Bountiful.id("clipboard_copy")
        )

        Kambrik.Message.registerClientMessage(
            OnBountyComplete.serializer(),
            OnBountyComplete::class,
            Bountiful.id("play_sound_on_client")
        )

        Kambrik.Message.registerClientMessage(
            UpdateBountyCriteriaObjective.serializer(),
            UpdateBountyCriteriaObjective::class,
            Bountiful.id("update_bounty_criteria")
        )

        Kambrik.Message.registerClientMessage(
            UpdateBountyTooltipNotification.serializer(),
            UpdateBountyTooltipNotification::class,
            Bountiful.id("update_bounty_tooltip")
        )
    }
}