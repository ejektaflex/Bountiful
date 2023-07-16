package io.ejekta.bountiful.bridge

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.messages.*
import io.ejekta.bountiful.util.iterateBountyStacks
import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.minecraft.advancement.criterion.EnterBlockCriterion
import net.minecraft.advancement.criterion.TickCriterion
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.TameableEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

interface BountifulSharedApi {

    fun isModLoaded(id: String): Boolean

    fun registerItemDynamicTextures() {
        ModelPredicateProviderRegistry.register(
            BountifulContent.BOUNTY_ITEM,
            Bountiful.id("rarity")
        ) { stack, clientWorld, livingEntity, seed ->
            BountyInfo[stack].rarity.ordinal.toFloat() / 10f
        }

        ModelPredicateProviderRegistry.register(
            BountifulContent.DECREE_ITEM,
            Bountiful.id("status")
        ) { stack, clientWorld, livingEntity, seed ->
            val data = DecreeData[stack]
            if (data.ids.isNotEmpty()) 1f else 0f
        }
    }

    fun registerServerMessages() {
        Kambrik.Message.registerServerMessage(
            SelectBounty.serializer(),
            SelectBounty::class,
            Bountiful.id("select_bounty")
        )
    }

    fun registerClientMessages() {
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
    }

    fun handleEntityKills(world: ServerWorld, entity: Entity, killedEntity: LivingEntity) {
        if (entity is LivingEntity) {
            val playerList = mutableListOf<ServerPlayerEntity>()
            playerList.addAll(world.getPlayers { it.distanceTo(entity) < 12f })
            playerList.addAll(world.getPlayers { it.distanceTo(killedEntity) < 12f })
            (entity as? ServerPlayerEntity)?.let { playerList.add(it) }

            if (entity is TameableEntity) {
                val owner = entity.owner as? ServerPlayerEntity
                owner?.let { playerList.add(it) }
            }

            (killedEntity.attacker as? ServerPlayerEntity)?.let { playerList.add(it) }
            (killedEntity.attacking as? ServerPlayerEntity)?.let { playerList.add(it) }

            playerList.toSet().forEach {
                BountyTypeRegistry.ENTITY.incrementEntityBounties(it, killedEntity)
            }
        }
    }

    // Update Criterion bounties
    fun registerCriterionStuff() {
        Kambrik.Criterion.subscribe { player, criterion, predicate ->
            if (criterion !is TickCriterion && criterion !is EnterBlockCriterion) {
                player.iterateBountyStacks {
                    val data = BountyData[this]

                    val triggerObjs = data.objectives.filter { it.critConditions != null }.takeIf { it.isNotEmpty() }
                        ?: emptyList()

                    for (obj in triggerObjs) {

                        val result = Kambrik.Criterion.testAgainst(
                            criterion,
                            Kambrik.Criterion.createCriterionConditionsFromJson(
                                buildJsonObject {
                                    put("trigger", obj.content)
                                    put("conditions", obj.critConditions ?: buildJsonObject {  })
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

                            BountyData[this] = data // update server with new data
                        }
                    }

                    data.checkForCompletionAndAlert(player, this)

                }
            }
        }
    }

}