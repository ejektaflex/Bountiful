package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyType
import io.ejekta.bountiful.content.BountyItem
import io.ejekta.bountiful.util.iterateBountyData
import io.ejekta.bountiful.util.iterateBountyStacks
import io.ejekta.kambrik.ext.identifier
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry


object EntityLogic : IEntryLogic {

    fun getEntityType(entry: BountyDataEntry): EntityType<*> {
        return Registry.ENTITY_TYPE.get(Identifier(entry.content))
    }

    fun getEntityKills(entry: BountyDataEntry): Int {
        return entry.tracking.getInt(entityKillsKey)
    }

    fun setEntityKills(entry: BountyDataEntry, value: Int) {
        entry.tracking.putInt(entityKillsKey, value)
    }

    override fun verifyValidity(entry: BountyDataEntry, player: PlayerEntity): MutableText? {
        val id = getEntityType(entry).identifier
        if (id != Identifier(entry.content)) {
            return Text.literal("* '${entry.content}' is not a valid entity!")
        }
        return null
    }

    override fun textSummary(entry: BountyDataEntry, isObj: Boolean, player: PlayerEntity): MutableText {
        val progress = getProgress(entry, player)
        return when (isObj) {
            true -> Text.literal("Kill ").append(
                getEntityType(entry).name.copy()
            ).formatted(progress.color).append(
                progress.neededText.colored(Formatting.WHITE)
            )
            false -> error("Cannot have an entity (${entry.content}) as a reward.")
        }
    }

    override fun textBoard(entry: BountyDataEntry, player: PlayerEntity): List<Text> {
        return listOf(getEntityType(entry).name)
    }

    override fun getProgress(entry: BountyDataEntry, player: PlayerEntity): Progress {
        return Progress(getEntityKills(entry), entry.amount)
    }

    override fun tryFinishObjective(entry: BountyDataEntry, player: PlayerEntity): Boolean {
        return getEntityKills(entry) >= entry.amount
    }

    override fun giveReward(entry: BountyDataEntry, player: PlayerEntity): Boolean {
        return false
    }

    private const val entityKillsKey = "kills"

    fun incrementEntityBounties(playerEntity: ServerPlayerEntity, killedEntity: LivingEntity) {
        playerEntity.iterateBountyData {
            var didWork = false
            val entityObjectives = objectives.filter { it.type == BountyType.ENTITY }
            if (entityObjectives.isEmpty()) return@iterateBountyData false
            for (obj in entityObjectives) {
                if (obj.content == killedEntity.type.identifier.toString()) {
                    obj.tracking.putInt(
                        entityKillsKey,
                        (obj.tracking.getInt(entityKillsKey) + 1).coerceAtMost(obj.amount)
                    )
                    didWork = true
                }
            }
            return@iterateBountyData didWork
        }
    }

}