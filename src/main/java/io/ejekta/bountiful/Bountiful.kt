@file:UseSerializers(IdentitySer::class)
package io.ejekta.bountiful

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.bounty.types.IBountyObjective
import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.messages.SelectBounty
import io.ejekta.bountiful.content.messages.UpdateBountyData
import io.ejekta.bountiful.util.isClientSide
import io.ejekta.bountiful.util.iterateBountyStacks
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.serial.serializers.IdentitySer
import kotlinx.serialization.UseSerializers
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.advancement.criterion.EnterBlockCriterion
import net.minecraft.advancement.criterion.TickCriterion
import net.minecraft.nbt.NbtCompound
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.registry.RegistryKey


class Bountiful : ModInitializer {

    companion object {
        const val ID = "bountiful"
        fun id(str: String) = Identifier(ID, str)
        val LOGGER = Kambrik.Logging.createLogger(ID)
        val BOUNTY_LOGIC_REGISTRY_KEY = RegistryKey.ofRegistry<IBountyType>(Bountiful.id("logic_registry"))
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
            if (entity is ServerPlayerEntity) {
                val playersInAction = world.getPlayers { it.distanceTo(entity) < 12f } + world.getPlayers { it.distanceTo(killedEntity) < 12f } + entity
                playersInAction.toSet().forEach {
                    BountyTypeRegistry.ENTITY.incrementEntityBounties(it, killedEntity)
                }
            }
        })

        // Update Item / Item Tag Bounties
        Kambrik.Criterion.addCriterionHandler("""
            {"trigger": "minecraft:inventory_changed"}
        """.trimIndent()) {
            iterateBountyStacks {
                val info = BountyInfo[this]
                // If we have an item/item-tag bounty, update it
                if (setOf(BountyTypeRegistry.ITEM.id, BountyTypeRegistry.ITEM_TAG.id).intersect(info.objectiveFlags).isNotEmpty()) {

                    val data = BountyData[this]

                    var objChanged = false
                    var numPreviouslyCompleted = 0
                    var numNowCompleted = 0
                    var numNewlyCompleted = 0
                    for (obj in data.objectives) {
                        if (obj.logicId in setOf(BountyTypeRegistry.ITEM.id, BountyTypeRegistry.ITEM_TAG.id)) {
                            val oldCurrent = obj.current
                            val newCurrent = (obj.logic as IBountyObjective).getNewCurrent(obj, this@addCriterionHandler)
                            println("Checked $oldCurrent against $newCurrent")
                            if (oldCurrent != newCurrent) {
                                println("We changed $obj, $oldCurrent to $newCurrent")
                                if (oldCurrent >= obj.amount) {
                                    numPreviouslyCompleted++
                                }
                                if (obj.amount in (oldCurrent + 1)..newCurrent) {
                                    numNewlyCompleted++
                                }
                                if (newCurrent >= obj.amount) {
                                    numNowCompleted++
                                }
                                objChanged = true
                            }
                        }
                    }

                    // If we crossed the threshold into completing them all
                    if (data.objectives.size in (numPreviouslyCompleted + 1)..numNowCompleted) {
                        println("We finished the bounty!")
                        this@addCriterionHandler.playSound(SoundEvents.ENTITY_GOAT_SCREAMING_DEATH, 1f, 1f)
                    } else if (numNowCompleted > numPreviouslyCompleted) {
                        println("We completed a new bounty!")
                        this@addCriterionHandler.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                    }

                    if (objChanged) {
                        // TODO send a ping sound?
                    }

                }
            }
        }

        // Update Criterion bounties
        Kambrik.Criterion.subscribe { player, criterion, predicate ->
            if (criterion !is TickCriterion && criterion !is EnterBlockCriterion) {
                player.iterateBountyStacks {
                    val data = BountyData[this]
                    val triggerObjs = data.objectives.filter { it.criteria != null }.takeIf { it.isNotEmpty() }
                        ?: return@iterateBountyStacks

                    for (obj in triggerObjs) {
                        val trigger = obj.criteria!!
                        println("Handling bounty trigger objective")

                        val result = Kambrik.Criterion.testAgainst(
                            criterion,
                            Kambrik.Criterion.createCriterionConditionsFromJson(trigger.criterion) ?: continue,
                            predicate
                        )

                        println("RESULT: $result")

                        if (result) {
                            println("Do something I guess")
                            obj.current += 1
                            UpdateBountyData(
                                player.inventory.indexOf(this),
                                NbtCompound().apply {
                                    put("payload", BountyData.encode(data))
                                }
                            ).sendToClient(player)

                            if (!isClientSide()) {
                                BountyData[this] = data // update server with new data
                            }
                        }
                    }

                    return@iterateBountyStacks
                }
            }
        }

    }

}