package io.ejekta.bountiful.bridge

import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.serialization.JsonOps
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.villager.DecreeTradeFactory
import io.ejekta.bountiful.messages.*
import io.ejekta.bountiful.util.iterateBountyStacks
import io.ejekta.kambrik.Kambrik
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.criterion.EnterBlockTrigger
import net.minecraft.advancements.criterion.PlayerTrigger
import net.minecraft.advancements.criterion.SimpleCriterionTrigger
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import kotlin.jvm.optionals.getOrNull

typealias GsonObject = JsonObject

interface BountifulSharedApi {

    fun isModLoaded(id: String): Boolean

    fun registerItemDynamicTextures() {
        // 26.1.2 uses the new item model system. We should set model selection via item models
        // or data components instead of predicate registration. Keeping this as a no-op for now.
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
                Identifier.parse("bountiful:village/common/bounty_gazebo"),
                Identifier.parse("minecraft:village/$villageType/houses"),
                Identifier.parse("bountiful:$villageType"),
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

    fun modifyTradeList(list: MutableList<*>) {
        // 26.1.2 trades are data-driven; trade injection needs a new path.
        // Leave as a no-op for now to keep compatibility across loaders.
    }

    // Update Criterion bounties
    fun registerCriterionStuff() {
        Kambrik.Criterion.subscribe { player, trigger, predicate ->
            if (trigger !is PlayerTrigger && trigger !is EnterBlockTrigger) {
                player.iterateBountyStacks {
                    val triggerObjs = objs.filter { it.criteriaJson != null }.takeIf { it.isNotEmpty() } ?: emptyList()

                    for (obj in triggerObjs) {

                        // Find the trigger in the registry
                        val objTrigger = BuiltInRegistries.TRIGGER_TYPES.getOptional(Identifier.parse(obj.content)).getOrNull()
                        // If it cannot be found, or is different from the 'launching' trigger, skip evaluation
                        if (objTrigger == null || objTrigger::class != trigger::class) {
                            continue
                        }

                        val regOps = RegistryOps.create(JsonOps.INSTANCE, player.level().registryAccess())

                        // TODO on resource reload, re-'compile' each JSON block only once with the server and avoid this cost
                        val gs = GsonObject().apply {
                            add("trigger", JsonPrimitive(obj.content))
                            add("conditions", obj.criteriaJson ?: JsonNull.INSTANCE)
                        }

                        val decoded = Criterion.CODEC.decode(regOps, gs)

                        val resulting = decoded.result().getOrNull()

                        if (resulting == null) {
                            Bountiful.LOGGER.warn("Failed to parse Criteria objective '${obj.id}': ${decoded.resultOrPartial()}")
                            continue
                        }

                        val triggerInstance = resulting.first.triggerInstance
                        val castTriggerInstance = triggerInstance as? SimpleCriterionTrigger.SimpleInstance

                        if (triggerInstance == null) {
                            Bountiful.LOGGER.error("Could not parse trigger instance for obj '${obj.id}'")
                            continue
                        }

                        val result = predicate.test(castTriggerInstance!!)

                        if (result) {
                            // Advance on server
                            advance(obj)
                            // Send advance to client
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
