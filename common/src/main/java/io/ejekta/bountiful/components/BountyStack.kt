package io.ejekta.bountiful.components

import io.ejekta.bountiful.bounty.types.IBountyObjective
import io.ejekta.bountiful.bounty.types.IBountyReward
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.messages.OnBountyComplete
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

class BountyStack(val stack: ItemStack) {

    var completion: Map<BountyDataEntry, Int>
        get() {
            val completionMap = stack[BountifulContent.BOUNTY_COMPLETION]?.amounts ?: emptyMap()
            return objs.associateWith { (completionMap[it.id] ?: 0) }
        }
        set(value) { stack[BountifulContent.BOUNTY_COMPLETION] = BountyCompletion(value.mapKeys { it.key.id }) }

    var info: BountyInfo
        get() = stack[BountifulContent.BOUNTY_INFO] ?: BountyInfo.EMPTY
        set(value) { stack[BountifulContent.BOUNTY_INFO] = value }

    var objs: List<BountyDataEntry>
        get() = stack[BountifulContent.BOUNTY_OBJS]?.entries ?: emptyList()
        set(value) { stack[BountifulContent.BOUNTY_OBJS] = BountyEntries(value) }

    var rews: List<BountyDataEntry>
        get() = stack[BountifulContent.BOUNTY_REWS]?.entries ?: emptyList()
        set(value) { stack[BountifulContent.BOUNTY_REWS] = BountyEntries(value) }

    var ping: Boolean
        get() = stack[BountifulContent.BOUNTY_PING]?.complete ?: false
        set(value) { stack[BountifulContent.BOUNTY_PING] = BountyPing(value) }

    fun progressOf(entry: BountyDataEntry): Int {
        return completion[entry] ?: 0
    }

    fun advance(entry: BountyDataEntry) {
        completion = completion.toMutableMap().apply {
            this[entry] = (this[entry] ?: 0) + 1
        }
    }

    fun setPickedUp(time: Long) {
        info = info.copy(timePickedUp = time)
    }

    // Objectives

    private fun hasFinishedObjectives(player: Player): Boolean {
        return objs.all {
            (it.logic as IBountyObjective).getProgress(it, player, progressOf(it)).isComplete()
        }
    }

    private fun consumeObjectives(player: Player): Boolean {
        return objs.all {
            (it.logic as IBountyObjective).consumeObjectives(it, player, progressOf(it))
        }
    }

    private fun isDone(player: Player): Boolean {
        return objs.all {
            (it.logic as IBountyObjective).getProgress(it, player, progressOf(it)).isComplete()
        } && ((info.timeLeftTicks(player.level())) > 0)
    }

    // Rewards
    private fun rewardPlayer(player: Player) {
        // Play XP pickup sound
        player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 1f)

        // Give XP to player
        player.giveExperiencePoints(rews.sumOf { (it.rarity.ordinal) * 2 + 1 })

        for (reward in rews) {
            (reward.logic as IBountyReward).giveReward(reward, player)
        }
    }

    // Awarding

    fun tryCashIn(player: Player): Boolean {
        if (info.timeLeftTicks(player.level()) <= 0) {
            player.displayClientMessage(Component.translatable("bountiful.bounty.expired"), false)
            return false
        }
        return if (hasFinishedObjectives(player)) {
            consumeObjectives(player)
            rewardPlayer(player)
            stack.shrink(stack.maxStackSize)
            true
        } else {
            player.displayClientMessage(Component.translatable("bountiful.tooltip.requirements"), false)
            false
        }
    }

    fun checkForCompletionAndAlert(player: Player) {
        if (isDone(player)) {
            if (!ping) {
                ping = true
                val playAction = OnBountyComplete(1f, 1f)

                if (player is ServerPlayer) {
                    playAction.sendToClient(player)
                } else {
                    playAction.runLocally(player)
                }
            } else {
                // do nothing was already complete
            }
        } else {
            ping = false
        }
    }

    fun genTooltip(isServer: Boolean, flag: TooltipFlag): List<MutableComponent> {
        if (isServer) {
            return emptyList()
        }
        val player = Minecraft.getInstance().player!!
        return buildList {
            add(Component.translatable("bountiful.tooltip.required").withStyle(ChatFormatting.GOLD).append(":"))
            addAll(objs.map {
                it.textOnBounty(player, true, progressOf(it))
            })
            add(Component.translatable("bountiful.tooltip.rewards").withStyle(ChatFormatting.GOLD).append(":"))
            addAll(rews.map {
                it.textOnBounty(player, false, progressOf(it))
            })
            if (flag == TooltipFlag.ADVANCED && BountifulIO.configData.client.advancedDebugTooltips) {
                add(Component.literal(""))
                add(Component.literal("Bountiful Debug Info:").withStyle(ChatFormatting.GOLD))
                add(Component.literal("Taken: ${info.timeTakenSecs(player.level())}, Left: ${info.timeLeftSecs(player.level())}"))
            }
        }
    }

}