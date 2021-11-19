package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyType
import io.ejekta.bountiful.content.BountyItem
import io.ejekta.kambrik.ext.identifier
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry


class EntityLogic(override val entry: BountyDataEntry) : IEntryLogic {

    val entityType: EntityType<*>
        get() = Registry.ENTITY_TYPE.get(Identifier(entry.content))

    var entityKills: Int
        get() = entry.tracking.getInt(entityKillsKey)
        set(value) {
            entry.tracking.putInt(entityKillsKey, value)
        }

    override fun verifyValidity(player: PlayerEntity): MutableText? {
        val id = entityType.identifier
        if (id != Identifier(entry.content)) {
            return LiteralText("* '${entry.content}' is not a valid entity!")
        }
        return null
    }

    override fun textSummary(isObj: Boolean, player: PlayerEntity): Text {
        val progress = getProgress(player)
        return when (isObj) {
            true -> LiteralText("Kill ").append(
                entityType.name.copy()
            ).formatted(progress.color).append(
                progress.neededText.colored(Formatting.WHITE)
            )
            false -> error("Cannot have an entity (${entry.content}) as a reward.")
        }
    }

    override fun textBoard(player: PlayerEntity): List<Text> {
        return listOf(entityType.name)
    }

    override fun getProgress(player: PlayerEntity): Progress {
        return Progress(entityKills, entry.amount)
    }

    override fun tryFinishObjective(player: PlayerEntity): Boolean {
        return entityKills >= entry.amount
    }

    override fun giveReward(player: PlayerEntity): Boolean {
        return false
    }

    companion object {

        private const val entityKillsKey = "kills"

        fun incrementEntityBounties(playerEntity: ServerPlayerEntity, killedEntity: LivingEntity) {
            playerEntity.inventory.main.filter {
                it.item is BountyItem
            }.forEach { stack ->
                BountyData.editIf(stack) {
                    var didWork = false
                    val entityObjectives = objectives.filter { it.type == BountyType.ENTITY }
                    if (entityObjectives.isEmpty()) return@editIf false
                    for (obj in entityObjectives) {
                        if (obj.content == killedEntity.type.identifier.toString()) {
                            obj.tracking.putInt(
                                entityKillsKey,
                                (obj.tracking.getInt(entityKillsKey) + 1).coerceAtMost(obj.amount)
                            )
                            didWork = true
                        }
                    }
                    return@editIf didWork
                }
            }
        }

    }

}