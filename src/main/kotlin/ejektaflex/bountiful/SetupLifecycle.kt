package ejektaflex.bountiful

//import ejektaflex.bountiful.worldgen.JigsawJank
import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.data.bounty.BountyEntryEntity
import ejektaflex.bountiful.data.json.BountyReloadListener
import ejektaflex.bountiful.data.json.JsonSerializers
import ejektaflex.bountiful.data.structure.DecreeList
import ejektaflex.bountiful.data.structure.EntryPool
import ejektaflex.bountiful.ext.sendErrorMsg
import ejektaflex.bountiful.ext.sendMessage
import ejektaflex.bountiful.ext.toData
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.item.ItemDecree
import ejektaflex.bountiful.worldgen.JigsawHelper
import net.minecraft.command.CommandSource
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.AnvilUpdateEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS
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

        FORGE_BUS.register(this)

        FORGE_BUS.addListener(::onCommonSetup)

        FORGE_BUS.addListener<FMLServerAboutToStartEvent> { event ->
            if (BountifulConfig.SERVER.villageGen.get()) {
                listOf("plains", "savanna", "snowy", "taiga", "desert").forEach { villageType ->
                    BountifulMod.logger.info("Registering Bounty Board Jigsaw Piece for Village Type: $villageType")
                    JigsawHelper.registerJigsaw(
                        event.server,
                        ResourceLocation("bountiful:village/common/bounty_gazebo"),
                        ResourceLocation("minecraft:village/$villageType/houses"),
                        BountifulConfig.SERVER.villageGenRate.get()
                    )
                }

                if (ModList.get().isLoaded("repurposed_structures")) {
                    BountifulMod.logger.info("Registering Bounty Board Jigsaw Piece for Repurposed Structures")
                    listOf("badlands", "birch", "crimson", "dark_forest", "giant_taiga", "jungle", "mountains",
                    "mushroom", "oak", "swamp", "warped").forEach { villageType ->
                        JigsawHelper.registerJigsaw(
                            event.server,
                            ResourceLocation("bountiful:village/common/bounty_gazebo"),
                            ResourceLocation("repurposed_structures:village/$villageType/houses"),
                            BountifulConfig.SERVER.villageGenRate.get()
                        )
                    }
                }

            }
        }

        JsonSerializers.register()

        //BountifulTriggers.register()
        BountifulStats.init()
    }


    fun validatePool(pool: EntryPool, sender: CommandSource? = null, log: Boolean = false): MutableList<BountyEntry> {

        sender?.sendMessage("Validating pool '${pool.id}'")

        val invalidEntries = mutableListOf<BountyEntry>()

        if (log) {
            BountifulMod.logger.info("Pool: ${pool.id}")
        }
        for (entry in pool.content) {
            if (log) {
                BountifulMod.logger.info("* $entry")
            }
            try {
                entry.validate()
            } catch (e: Exception) {
                sender?.sendErrorMsg(e.message!!)
                invalidEntries.add(entry)
            }
        }

        return invalidEntries
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

    @SubscribeEvent @JvmStatic
    fun onCommonSetup(event: FMLServerAboutToStartEvent) {
        // Register villager generation jigsaws

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


