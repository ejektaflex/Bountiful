package ejektaflex.bountiful

import com.mojang.datafixers.util.Pair
import ejektaflex.bountiful.advancement.BountifulTriggers
import ejektaflex.bountiful.block.BoardTileEntity
import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.data.bounty.BountyEntryEntity
import ejektaflex.bountiful.data.bounty.enums.BountifulResourceType
import ejektaflex.bountiful.data.json.BountyReloadListener
import ejektaflex.bountiful.data.json.JsonSerializers
import ejektaflex.bountiful.data.structure.DecreeList
import ejektaflex.bountiful.data.structure.EntryPool
import ejektaflex.bountiful.ext.getUnsortedList
import ejektaflex.bountiful.ext.sendErrorMsg
import ejektaflex.bountiful.ext.sendMessage
import ejektaflex.bountiful.ext.toData
import ejektaflex.bountiful.gui.BoardContainer
import ejektaflex.bountiful.gui.BoardScreen
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.item.ItemDecree
//import ejektaflex.bountiful.worldgen.JigsawJank
import net.minecraft.block.Block
import net.minecraft.client.gui.ScreenManager
import net.minecraft.command.CommandSource
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.Item
import net.minecraft.item.ItemModelsProperties
import net.minecraft.item.ItemStack
import net.minecraft.resources.IResourceManager
import net.minecraft.resources.IResourceManagerReloadListener
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.ResourceLocation
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece
import net.minecraftforge.common.BasicTrade
import net.minecraftforge.common.extensions.IForgeContainerType
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.AnvilUpdateEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.village.VillagerTradesEvent
import net.minecraftforge.event.village.WandererTradesEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DeferredWorkQueue
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.util.function.Supplier
import kotlin.math.min

@Mod.EventBusSubscriber
object SetupLifecycle {

    val reloadListener = BountyReloadListener()

    init {
        BountifulMod.logger.info("Loading Bountiful listeners..")
        BountifulContent.TileEntityRegistry.register(MOD_BUS)
        BountifulContent.BlockRegistry.register(MOD_BUS)
        BountifulContent.ContainerRegistry.register(MOD_BUS)
        BountifulContent.ItemRegistry.register(MOD_BUS)
    }

    @SubscribeEvent
    fun gameSetup(event: FMLCommonSetupEvent) {
        JsonSerializers.register()

        if (BountifulConfig.SERVER.villageGen.get()) {

            val injectList = listOf(
                    "village/plains/houses",
                    "village/desert/houses",
                    "village/savanna/houses",
                    "village/taiga/houses",
                    "village/snowy/houses"
            )

            // TODO Re-implement village generation

            /*
            for (place in injectList) {
                JigsawJank.create().append(ResourceLocation("minecraft", place)) {
                    listOf(Pair.of(SingleJigsawPiece("bountiful:village/common/bounty_gazebo"),
                            BountifulConfig.SERVER.villageGenRate.get()
                    ))
                }
            }

             */

        }

        //BountifulTriggers.register()
        BountifulStats.init()

    }

    fun validatePool(pool: EntryPool, sender: CommandSource? = null, log: Boolean = false): MutableList<BountyEntry> {

        sender?.sendMessage("Validating pool '${pool.id}'")

        val validEntries = mutableListOf<BountyEntry>()

        if (log) {
            BountifulMod.logger.info("Pool: ${pool.id}")
        }
        for (entry in pool.content) {
            if (log) {
                BountifulMod.logger.info("* $entry")
            }
            try {
                entry.validate()
                validEntries.add(entry)
            } catch (e: Exception) {
                sender?.sendErrorMsg(e.message!!)
            }
        }

        return validEntries
    }

    // Update mob bounties
    @SubscribeEvent
    fun entityLivingDeath(e: LivingDeathEvent) {
        val deadEntity = e.entityLiving


        if (e.source.trueSource is PlayerEntity) {
            val player = e.source.trueSource as PlayerEntity

            if (BountifulConfig.SERVER.coopKillsCount.get()) {

                val withinRange = player.world.players.filter {
                    it.getDistance(player) <= BountifulConfig.SERVER.coopKillDistance.get() ||
                            it.getDistance(deadEntity) <= BountifulConfig.SERVER.coopKillDistance.get()
                }

                withinRange.forEach {
                    updateBountiesForEntity(it, deadEntity)
                }

            } else {
                updateBountiesForEntity(player, deadEntity)
            }

        }
    }

    private fun updateBountiesForEntity(player: PlayerEntity, deadEntity: LivingEntity) {
        val bountyStacks = player.inventory.mainInventory.filter { it.item is ItemBounty && it.hasTag() }
        if (bountyStacks.isNotEmpty()) {
            bountyStacks.forEach { stack ->
                val data = stack.toData(::BountyData)
                val eObjs = data.objectives.content.filterIsInstance<BountyEntryEntity>()
                for (obj in eObjs) {
                    if (obj.isSameEntity(deadEntity)) {
                        obj.killedAmount = min(obj.killedAmount + 1, obj.amount)
                        stack.tag = data.serializeNBT()
                    }
                }
            }
        }
    }


    @SubscribeEvent
    fun onReloadData(event: AddReloadListenerEvent) {
        BountifulMod.logger.info("Bountiful adding resource listener..")
        event.addListener(reloadListener)
    }

    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        BountifulCommand.generateCommand(event.dispatcher)
    }



    @SubscribeEvent
    fun anvilEvent(event: AnvilUpdateEvent) {
        val result = ItemDecree.combine(event.left, event.right)
        if (result != null) {
            event.output = result
            event.cost = 5 + (event.output.toData(::DecreeList).ids.size * 5)
        }
    }

    // TODO reimplement wandering decree trades

    /*
    private val decreeTrade = BasicTrade(3, ItemStack(BountifulContent.Items.DECREE), 5, 5, 0.5f)

    @SubscribeEvent
    fun doWandererTrades(event: WandererTradesEvent) {
        event.genericTrades.add(decreeTrade)
    }

    @SubscribeEvent
    fun doVillagerTrades(event: VillagerTradesEvent) {
        event.trades[2].add(decreeTrade)
    }

     */

}


