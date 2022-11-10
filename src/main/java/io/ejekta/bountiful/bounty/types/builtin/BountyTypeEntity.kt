package io.ejekta.bountiful.bounty.types.builtin

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bounty.types.IBountyObjective
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


class BountyTypeEntity : IBountyObjective {

    override val id: Identifier = Identifier("entity")

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

    fun incrementEntityBounties(playerEntity: ServerPlayerEntity, killedEntity: LivingEntity) {
        playerEntity.iterateBountyStacks {
            val info = BountyInfo[this]
            if (this@BountyTypeEntity.id in info.objectiveFlags) {
                val data = BountyData[this]
                val entityObjectives = data.objectives.filter { it.logicId == this@BountyTypeEntity.id }
                var didChange = false
                for (obj in entityObjectives) {
                    if (obj.content == killedEntity.type.identifier.toString()) {
                        obj.current += 1
                        didChange = true
                    }
                }
                // Update tooltip if changed
                if (didChange) {
                    BountyInfo[this] = info.update(data)
                }
            }
        }
    }


    companion object {
        fun getEntityType(entry: BountyDataEntry): EntityType<*> {
            return Registry.ENTITY_TYPE.get(Identifier(entry.content))
        }
    }

}