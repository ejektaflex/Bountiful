package io.ejekta.bountiful.bridge

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.components.GsonObject
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.villager.DecreeTradeFactory
import io.ejekta.bountiful.messages.*
import io.ejekta.bountiful.util.iterateBountyStacks
import io.ejekta.kambrik.Kambrik
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.minecraft.advancements.critereon.EnterBlockTrigger
import net.minecraft.advancements.critereon.PlayerTrigger
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.npc.VillagerTrades
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item

interface BountifulSharedApi {

    fun isModLoaded(id: String): Boolean

    fun registerItemDynamicTextures() {
        ItemProperties.register(
            BountifulContent.BOUNTY_ITEM,
            Bountiful.id("rarity")
        ) { stack, clientWorld, livingEntity, seed ->
            (stack[BountifulContent.BOUNTY_INFO]?.rarity?.ordinal?.toFloat() ?: 0f) / 10f
        }

        ItemProperties.register(
            BountifulContent.DECREE_ITEM,
            Bountiful.id("status")
        ) { stack, clientWorld, livingEntity, seed ->
            val data = stack[BountifulContent.DECREE_DATA]
            if ((data?.ids ?: emptySet()).isNotEmpty()) 1f else 0f
        }
    }

    fun registerServerMessages() {
        Kambrik.Message.registerServerMessage<SelectBounty>(Bountiful.id("select_bounty"))
        Kambrik.Message.registerServerMessage<ServerPlayerStatus>(Bountiful.id("server_player_status"))
    }

    fun registerClientMessages() {
        Kambrik.Message.registerClientMessage<ClipboardCopy>(Bountiful.id("clipboard_copy"))
        Kambrik.Message.registerClientMessage<OnBountyComplete>(Bountiful.id("bounty_complete"))
        Kambrik.Message.registerClientMessage<UpdateCriteriaObjective>(Bountiful.id("update_bounty_criteria"))
        Kambrik.Message.registerClientMessage<ClientPlayerStatus>(Bountiful.id("client_player_status"))
    }

    fun registerJigsawPieces(server: MinecraftServer) {
        listOf("plains", "savanna", "snowy", "taiga", "desert").forEach { villageType ->
            Bountiful.LOGGER.info("Registering Bounty Board Jigsaw Piece for Village Type: $villageType")
            Kambrik.Structure.addToStructurePool(
                server,
                ResourceLocation.parse("bountiful:village/common/bounty_gazebo"),
                ResourceLocation.parse("minecraft:village/$villageType/houses"),
                ResourceLocation.parse("bountiful:$villageType"),
                BountifulIO.configData.board.villageGenFrequency
            )
        }
    }

    fun handleEntityKills(world: ServerLevel, entity: Entity, killedEntity: LivingEntity) {
        if (entity is LivingEntity) {
            val playerList = mutableSetOf<ServerPlayer>()
            playerList.addAll(world.getPlayers { it.distanceTo(entity) < 12f || it.distanceTo(killedEntity) < 12f })
            (entity as? ServerPlayer)?.let { playerList.add(it) }

            if (entity is TamableAnimal) {
                val owner = entity.owner as? ServerPlayer
                owner?.let {
                    playerList.add(it)
                }
            }

            // Attacker
            (killedEntity.lastHurtByMob as? ServerPlayer)?.let { playerList.add(it) }
            // Attacking
            (killedEntity.lastHurtMob as? ServerPlayer)?.let { playerList.add(it) }

            playerList.forEach {
                (entity as? TamableAnimal)?.let { tamable ->
                    if (tamable.owner ==  it) {
                        BountifulContent.Triggers.FETCH_QUEST.trigger(it)
                    }
                }
                BountyTypeRegistry.ENTITY.incrementEntityBounties(it, killedEntity)
            }
        }
    }

    fun getItemGroups(): Map<ResourceKey<CreativeModeTab>, List<() -> Item>> {
        return mapOf(
            CreativeModeTabs.FUNCTIONAL_BLOCKS to listOf({ BountifulContent.BOARD_ITEM }, { BountifulContent.DECREE_ITEM }),
        )
    }

    fun modifyTradeList(list: MutableList<VillagerTrades.ItemListing>) {
        list.add(DecreeTradeFactory())
    }

    // Update Criterion bounties
    fun registerCriterionStuff() {
        Kambrik.Criterion.subscribe { player, trigger, predicate ->
            if (trigger !is PlayerTrigger && trigger !is EnterBlockTrigger) {
                player.iterateBountyStacks {
                    val triggerObjs = objs.filter { it.criteriaJson != null }.takeIf { it.isNotEmpty() } ?: emptyList()

                    for (obj in triggerObjs) {
                        val result = Kambrik.Criterion.testAgainst(
                            trigger,
                            Kambrik.Criterion.createCriterionConditionsFromGson(
                                GsonObject().apply {
                                    add("trigger", JsonPrimitive(obj.content))
                                    add("conditions", obj.criteriaJson ?: JsonNull.INSTANCE)
                                }
                            ) ?: continue,
                            predicate
                        )

                        if (result) {
                            advance(obj)
                            UpdateCriteriaObjective(player.inventory.findSlotMatchingItem(stack), obj.id).sendToClient(player)
                        }
                    }

                    checkForCompletionAndAlert(player)
                }
            }
        }
    }

    fun registerCompostables()

    fun getClassLoader(): ClassLoader {
        return Bountybridge.Companion::class.java.classLoader
    }

}