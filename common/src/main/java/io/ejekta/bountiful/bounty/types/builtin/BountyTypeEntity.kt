package io.ejekta.bountiful.bounty.types.builtin

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.bounty.types.IBountyObjective
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.util.iterateBountyStacks
import io.ejekta.kambrik.ext.identifier
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier


class BountyTypeEntity : IBountyObjective {

    override val id: Identifier = Identifier("entity")

    override fun isValid(entry: PoolEntry, server: MinecraftServer): Boolean {
        val id = getEntityType(Identifier(entry.content)).identifier
        return id == Identifier(entry.content)
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
            val data = BountyData[this]
            val entityObjs = data.objectives.filter { it.logicId == this@BountyTypeEntity.id }
            if (entityObjs.isNotEmpty()) {
                var changes = false
                for (obj in entityObjs) {
                    if (obj.content == killedEntity.type.identifier.toString()) {
                        obj.current += 1
                        changes = true
                    }
                }
                if (changes) {
                    BountyData[this] = data
                    data.checkForCompletionAndAlert(playerEntity, this)
                }
            }
        }
    }


    companion object {
        fun getEntityType(entry: BountyDataEntry): EntityType<*> {
            return getEntityType(Identifier(entry.content))
        }

        fun getEntityType(id: Identifier): EntityType<*> {
            return Registries.ENTITY_TYPE.get(id)
        }
    }

}