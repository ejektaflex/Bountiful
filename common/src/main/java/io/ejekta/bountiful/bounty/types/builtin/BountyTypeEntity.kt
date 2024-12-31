package io.ejekta.bountiful.bounty.types.builtin

import io.ejekta.bountiful.bounty.types.IBountyObjective
import io.ejekta.bountiful.components.BountyDataEntry
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.util.iterateBountyStacks
import io.ejekta.kambrik.ext.id
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player


class BountyTypeEntity : IBountyObjective {

    override val id: ResourceLocation = ResourceLocation.parse("entity")

    override fun isValid(entry: PoolEntry, server: MinecraftServer): Boolean {
        val id = getEntityType(ResourceLocation.parse(entry.content)).id
        return id == ResourceLocation.parse(entry.content)
    }

    override fun textOnBounty(entry: BountyDataEntry, isObj: Boolean, player: Player, current: Int): List<MutableComponent> {
        val progress = getProgress(entry, player, current)
        val result = when (isObj) {
            true -> Component.literal("Kill ").append(
                getEntityType(entry).description.copy()
            ).withStyle(progress.color).append(
                progress.neededText.colored(ChatFormatting.WHITE)
            )
            false -> Component.literal("ERR: Cannot have an entity (${entry.content}) as a reward.")
        }
        return listOf(result)
    }

    override fun textOnBoardSidebar(entry: BountyDataEntry, player: Player): List<Component> {
        return listOf(getEntityType(entry).description)
    }

    fun incrementEntityBounties(playerEntity: ServerPlayer, killedEntity: LivingEntity) {
        // The player cannot kill themselves (arrow, potion, etc) to complete a bounty
        if (playerEntity == killedEntity) {
            return
        }
        playerEntity.iterateBountyStacks {
            val entityObjs = objs.filter { it.logic.id == this@BountyTypeEntity.id }
            if (entityObjs.isNotEmpty()) {
                var changes = false
                for (obj in entityObjs) {
                    if (obj.content == killedEntity.type.id.toString()) {
                        advance(obj)
                        changes = true
                    }
                }
                if (changes) {
                    checkForCompletionAndAlert(playerEntity)
                }
            }
        }
    }


    companion object {
        fun getEntityType(entry: BountyDataEntry): EntityType<*> {
            return getEntityType(ResourceLocation.parse(entry.content))
        }

        fun getEntityType(id: ResourceLocation): EntityType<*> {
            return BuiltInRegistries.ENTITY_TYPE.get(id)
        }
    }

}