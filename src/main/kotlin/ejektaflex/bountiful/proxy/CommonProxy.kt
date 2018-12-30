package ejektaflex.bountiful.proxy

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.events.PopulateBountyBoardEvent
import ejektaflex.bountiful.config.BountifulIO
import ejektaflex.bountiful.data.DefaultData
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.logic.BountyChecker
import ejektaflex.bountiful.registry.BountyRegistry
import ejektaflex.bountiful.registry.RewardRegistry
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

open class CommonProxy : IProxy {

    @SubscribeEvent
    fun entityLivingDeath(e: LivingDeathEvent) {
        if (e.source.trueSource is EntityPlayer) {

            val player = e.source.trueSource as EntityPlayer
            val bountyStacks = player.inventory.mainInventory.filter { it.item is ItemBounty }
            if (bountyStacks.isNotEmpty()) {
                bountyStacks.forEach { stack ->
                    val data = (stack.item as ItemBounty).getBountyData(stack)
                    BountyChecker.tryTakeEntities(player, data, stack, e.entityLiving)
                }
            }
        }
    }

    // Cancel first posting to board on board creation
    @SubscribeEvent
    fun onBoardPost(e: PopulateBountyBoardEvent) {
        if (e.board.newBoard) {
            e.board.newBoard = false
            e.isCanceled = true
        }
    }

    override fun postInit(e: FMLPostInitializationEvent) {
        // Populate entries, fill if none exist
        "bounties.json".let {
            BountifulIO.populateConfigFolder(Bountiful.configDir, DefaultData.entries.items, it)
            val invalids = BountifulIO.hotReloadBounties(it)
            println("Invalid bounties: $invalids")
        }

        // Same for rewards
        "rewards.json".let {
            BountifulIO.populateConfigFolder(Bountiful.configDir, DefaultData.rewards.items.map { item ->
                item.genericPick
            }, it)
            val invalid = BountifulIO.hotReloadRewards(it)
            println("Invalid rewards: $invalid")
            //BountifulIO.hotReloadJson(RewardRegistry, it)
        }

        println("Bounties: ${BountyRegistry.items.size}")
        BountyRegistry.items.forEach { println(it) }

        println("Rewards: ${RewardRegistry.items.size}")
        RewardRegistry.items.forEach { println(it) }
    }

}