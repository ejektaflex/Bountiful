package ejektaflex.bountiful.data.bounty

import ejektaflex.bountiful.BountifulConfig
import ejektaflex.bountiful.data.bounty.checkers.CheckerRegistry
import ejektaflex.bountiful.data.bounty.enums.BountyNBT
import ejektaflex.bountiful.data.bounty.enums.BountyRarity
import ejektaflex.bountiful.data.bounty.enums.BountyType
import ejektaflex.bountiful.data.registry.DecreeRegistry
import ejektaflex.bountiful.data.structure.Decree
import ejektaflex.bountiful.ext.*
import ejektaflex.bountiful.util.ValueRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.NonNullList
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable
import java.util.*
import kotlin.math.ceil
import kotlin.math.max

class BountyData : INBTSerializable<CompoundNBT> {

    var boardStamp = maxTimeAtBoard
    var bountyTime = 0L
    var rarity = 0
    val objectives = ValueRegistry<BountyEntry>()
    val rewards = ValueRegistry<BountyEntry>()
    var bountyStamp: Long? = null

    fun timeTaken(world: World): Long {
        return world.gameTime - (bountyStamp ?: world.gameTime)
    }

    fun timeLeft(world: World): Long {
        return if (bountyStamp == null) {
            bountyTime
        } else {
            max(bountyStamp!! + bountyTime - world.gameTime, 0)
        }
    }

    fun hasExpired(world: World): Boolean {
        return timeLeft(world) <= 0
    }

    val rarityEnum: BountyRarity
        get() = BountyRarity.getRarityFromInt(rarity)

    fun boardTimeLeft(world: World): Long {
        return max(boardStamp + maxTimeAtBoard - world.gameTime, 0)
    }


    fun tooltipInfo(world: World, advanced: Boolean): List<ITextComponent> {
        val passed = CheckerRegistry.passedChecks(Minecraft.getInstance().player!!, this)

        val typeIds = BountyType.values().map { it.id }

        val objs = passed.toList().sortedBy {
            typeIds.indexOf(it.first.bType)
        }.map {
            (it.first as IBountyObjective).tooltipObjective(it.second)
        }

        val rews = rewards.content.map {
            (it as IBountyReward).tooltipReward()
        }


        return listOf(
                listOf(TranslationTextComponent("bountiful.tooltip.required").mergeStyle(TextFormatting.GOLD)) +
                        objs +
                        listOf(TranslationTextComponent("bountiful.tooltip.rewards").mergeStyle(TextFormatting.GOLD)) +
                        rews
        ).flatten()

    }

    fun remainingTime(world: World): String {
        return formatTimeExpirable(timeLeft(world) / bountyTickFreq)
    }

    private fun formatTickTime(n: Long): String {
        return if (n / 60 <= 0) {
            "${n}s"
        } else {
            "${n / 60}m ${n % 60}s"
        }
    }

    private fun formatTimeExpirable(n: Long): String {
        return if (n <= 0) {
            "§4${I18n.format("bountiful.tooltip.expired")}"
        } else {
            formatTickTime(n)
        }
    }


    override fun deserializeNBT(tag: CompoundNBT) {
        boardStamp = tag.getInt(BountyNBT.BoardStamp.key)
        bountyTime = tag.getLong(BountyNBT.BountyTime.key)
        rarity = tag.getInt(BountyNBT.Rarity.key)

        if (BountyNBT.BountyStamp.key in tag) {
            bountyStamp = tag.getLong(BountyNBT.BountyStamp.key)
        }

        objectives.restore(
                tag.getUnsortedList(BountyNBT.Objectives.key).map { it.toBountyEntry }
        )

        rewards.restore(
                tag.getUnsortedList(BountyNBT.Rewards.key).map { it.toBountyEntry }
        )
    }

    override fun serializeNBT(): CompoundNBT {
        return CompoundNBT().apply {
            putInt(BountyNBT.BoardStamp.key, boardStamp)
            putLong(BountyNBT.BountyTime.key, bountyTime)
            putInt(BountyNBT.Rarity.key, rarity)
            bountyStamp?.let { putLong(BountyNBT.BountyStamp.key, it) }
            setUnsortedList(BountyNBT.Objectives.key, objectives.content.toSet())
            setUnsortedList(BountyNBT.Rewards.key, rewards.content.toSet())
        }
    }

    private fun assignRewards(inRarity: BountyRarity, decrees: List<Decree>): Int {
        val toAdd = createRewards(inRarity, decrees)
        rarity = BountyRarity.values().indexOf(inRarity)
        rewards.add(*toAdd.toTypedArray())
        val assignedWorth = toAdd.sumBy { it.calculatedWorth }
        return (assignedWorth * inRarity.worthMult).toInt()
    }

    private fun assignObjectives(inRarity: BountyRarity, decrees: List<Decree>, worth: Int) {
        val objs = createObjectives(rewards.content, inRarity, decrees, worth)

        for (obj in objs) {
            bountyTime += if (obj.timeMult != null) {
                (worth * BountifulConfig.SERVER.timeMultiplier.get() * obj.timeMult!!).toLong()
            } else {
                (worth * BountifulConfig.SERVER.timeMultiplier.get()).toLong()
            }
        }

        bountyTime = max(bountyTime, BountifulConfig.SERVER.bountyTimeMin.get().toLong() * 20)

        objectives.add(*objs.toTypedArray())
    }

    companion object {
        private val rando = Random()
        const val bountyTickFreq = 20L
        const val boardTickFreq = 20L

        val maxTimeAtBoard: Int
            get() = BountifulConfig.SERVER.boardLifespan.get() * 20

        fun isValidBounty(stack: ItemStack): Boolean {
            return try {
                stack.toData(::BountyData)
                true
            } catch (e: Exception) {
                false
            }
        }

        fun create(inRarity: BountyRarity, decrees: List<Decree>): BountyData {
            val data = BountyData()

            val toSatisfy = data.assignRewards(inRarity, decrees) * BountifulConfig.SERVER.worthRatio.get()
            data.assignObjectives(inRarity, decrees, toSatisfy.toInt())

            return data
        }

        fun createRewards(inRarity: BountyRarity, decrees: List<Decree>): List<BountyEntry> {
            val rewards = DecreeRegistry.getRewards(decrees)
            var numRewards = (1..2).hackyRandom()
            val toAdd = mutableListOf<BountyEntry>()

            // Higher tier bounties will have a chance of having an extra reward
            if (rando.nextFloat() < inRarity.extraRewardChance) {
                numRewards++
            }

            for (i in 0 until numRewards) {

                val totalRewards = rewards.filter {
                    it.content !in toAdd.map { alreadyAdded -> alreadyAdded.content }
                }

                // Return if there's nothing to pick
                if (totalRewards.isEmpty()) {
                    break
                }

                toAdd.add(totalRewards.weightedRandomNorm(inRarity.exponent).pick())
            }

            return toAdd
        }

        fun getObjectivesWithinVariance(objs: List<BountyEntry>, worth: Int, variance: Double): List<BountyEntry> {
            val wRange = ceil(worth * variance)

            // Make sure to filter out non-objectives
            val objGroups = objs.groupBy { it.worthDistanceFrom(worth) }

            //println("Obj groups keys: " + objGroups.keys.toString())

            val groupsInRange = objGroups.filter { it.key <= wRange }
            val totalObjs = groupsInRange.values.flatten()

            return totalObjs
        }

        fun pickObjective(objectives: NonNullList<BountyEntry>, worth: Int): BountyEntry {

            val variance = 0.2

            val inVariance = getObjectivesWithinVariance(objectives, worth, variance)

            // If there are no objectives within variance from target worth, just get the one with the smallest distance
            // Otherwise, if one/some exist, pick at random.
            return if (inVariance.isEmpty()) {
                objectives.minByOrNull { it.worthDistanceFrom(worth) }!!.pick(worth)
            } else {
                inVariance.hackyRandom().pick(worth)
            }
        }

        fun createObjectives(rewards: List<BountyEntry>, inRarity: BountyRarity, decrees: List<Decree>, worth: Int): List<BountyEntry> {
            val rewardContentIds = rewards.map { it.content }

            val objectives = DecreeRegistry.getObjectives(decrees).filter {
                it.content !in rewardContentIds
            }

            var numObjectives = (1..2).hackyRandom()

            /*
            // Possible chance for higher tier bounties to get an additional objective
            var chanceToAddThirdObj = (1.0 - inRarity.exponent) / 2
            if (rando.nextFloat() < chanceToAddThirdObj) {
                numObjectives++
            }

             */

            if (objectives.isEmpty()) {
                return listOf()
            }

            val worthGroups = randomSplit(worth, numObjectives)

            val toAdd = mutableListOf<BountyEntry>()

            for (wrth in worthGroups) {

                // Filter out things already picked
                val pickableObjs = objectives.filter {
                    it.content !in toAdd.map { alreadyAdded -> alreadyAdded.content }
                }

                // Return if there's nothing to pick
                if (pickableObjs.isEmpty()) {
                    break
                }

                val closest = pickObjective(supposedlyNotNull(pickableObjs), wrth)

                toAdd.add(closest)
            }
            return toAdd
        }


    }


}