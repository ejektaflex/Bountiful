package ejektaflex.compat

import ejektaflex.bountiful.data.BountyData
import net.darkhax.gamestages.GameStageHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

object FacadeGameStages {
    fun anyPlayerHas(world: World, stages: List<String>): Boolean {
        val allPlayers = world.minecraftServer?.playerList?.players
        return allPlayers?.any { GameStageHelper.hasAllOf(it, stages) } ?: false
    }

    fun stagesStillNeededFor(player: EntityPlayer, bountyData: BountyData): List<String> {
        val bountyStagesNeeded = bountyData.toGet.items.map { it.stages }.flatten().toSet()
        val rewardStagesNeeded = bountyData.rewards.items.map { it.stages }.flatten().toSet()
        val allStagesNeeded = bountyStagesNeeded + rewardStagesNeeded
        println("Stages needed: $allStagesNeeded")
        val playerStages = GameStageHelper.getPlayerData(player).stages.filterNotNull().toSet()
        val stillNeeded = (allStagesNeeded - playerStages).toList()
        println("Stages still needed: $stillNeeded")
        return stillNeeded
    }

}