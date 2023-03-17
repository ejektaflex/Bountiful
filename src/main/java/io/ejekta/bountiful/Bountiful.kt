@file:UseSerializers(IdentitySer::class)
package io.ejekta.bountiful

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.messages.*
import io.ejekta.bountiful.util.isClientSide
import io.ejekta.bountiful.util.iterateBountyStacks
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.serial.serializers.IdentitySer
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.advancement.criterion.EnterBlockCriterion
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.advancement.criterion.TickCriterion
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier


class Bountiful : ModInitializer {

    companion object {
        const val ID = "bountiful"
        fun id(str: String) = Identifier(ID, str)
        val LOGGER = Kambrik.Logging.createLogger(ID)
        val BOUNTY_LOGIC_REGISTRY_KEY: RegistryKey<Registry<IBountyType>> = RegistryKey.ofRegistry(id("logic_registry"))
    }

    init {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(BountifulIO)
    }

    override fun onInitialize() {
        LOGGER.info("Common init")
        BountifulIO.loadConfig()

        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleEvents.ServerStarting { server ->
            listOf("plains", "savanna", "snowy", "taiga", "desert").forEach { villageType ->

                LOGGER.info("Registering Bounty Board Jigsaw Piece for Village Type: $villageType")

                Kambrik.Structure.addToStructurePool(
                    server,
                    Identifier("bountiful:village/common/bounty_gazebo"),
                    Identifier("minecraft:village/$villageType/houses"),
                    BountifulIO.configData.boardGenFrequency
                )

            }
        })

        Kambrik.Message.registerServerMessage(SelectBounty.serializer(), id("select_bounty"))

        // Increment entity bounties for all players within 12 blocks of the player and all players within 12 blocks of the mob
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(ServerEntityCombatEvents.AfterKilledOtherEntity { world, entity, killedEntity ->
            println("Entity killed, bounty update check..")
            if (entity is ServerPlayerEntity) {
                val playersInAction = world.getPlayers { it.distanceTo(entity) < 12f } + world.getPlayers { it.distanceTo(killedEntity) < 12f } + entity
                playersInAction.toSet().forEach {
                    BountyTypeRegistry.ENTITY.incrementEntityBounties(it, killedEntity)
                }
            }
        })

        // Update Criterion bounties
        Kambrik.Criterion.subscribe { player, criterion, predicate ->
            if (criterion !is TickCriterion && criterion !is EnterBlockCriterion) {
                player.iterateBountyStacks {
                    val data = BountyData[this]

                    val triggerObjs = data.objectives.filter { it.critConditions != null }.takeIf { it.isNotEmpty() }
                        ?: emptyList()

                    for (obj in triggerObjs) {
                        val conds = obj.critConditions!!

                        val result = Kambrik.Criterion.testAgainst(
                            criterion,
                            Kambrik.Criterion.createCriterionConditionsFromJson(
                                buildJsonObject {
                                    put("trigger", obj.content)
                                    put("conditions", conds)
                                }
                            ) ?: continue,
                            predicate
                        )

                        if (result) {
                            obj.current += 1
                            UpdateBountyCriteriaObjective(
                                player.inventory.indexOf(this),
                                data.objectives.indexOf(obj)
                            ).sendToClient(player)

                            if (!isClientSide()) {
                                BountyData[this] = data // update server with new data
                            }
                        }
                    }

                    data.checkForCompletionAndAlert(player, this)

                }
            }
        }

        Kambrik.Message.registerClientMessage(
            ClipboardCopy.serializer(),
            id("clipboard_copy")
        )

        Kambrik.Message.registerClientMessage(
            OnBountyComplete.serializer(),
            id("play_sound_on_client")
        )

        Kambrik.Message.registerClientMessage(
            UpdateBountyCriteriaObjective.serializer(),
            id("update_bounty_criteria")
        )

        Kambrik.Message.registerClientMessage(
            UpdateBountyTooltipNotification.serializer(),
            id("update_bounty_tooltip")
        )

    }

}