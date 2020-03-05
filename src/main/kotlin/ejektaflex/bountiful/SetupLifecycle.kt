package ejektaflex.bountiful

import ejektaflex.bountiful.api.data.IEntryPool
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.api.data.entry.BountyEntryEntity
import ejektaflex.bountiful.api.data.json.JsonAdapter
import ejektaflex.bountiful.api.data.json.JsonSerializers
import ejektaflex.bountiful.api.ext.sendErrorMsg
import ejektaflex.bountiful.api.ext.sendMessage
import ejektaflex.bountiful.block.BoardTE
import ejektaflex.bountiful.command.BountifulCommand
import ejektaflex.bountiful.content.ModContent
import ejektaflex.bountiful.data.*
import ejektaflex.bountiful.gui.BoardContainer
import ejektaflex.bountiful.gui.BoardScreen
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.item.ItemDecree
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.minecraft.block.Block
import net.minecraft.client.gui.ScreenManager
import net.minecraft.command.CommandSource
import net.minecraft.entity.merchant.villager.VillagerTrades
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.resources.IResourceManager
import net.minecraft.resources.IResourceManagerReloadListener
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.common.BasicTrade
import net.minecraftforge.common.extensions.IForgeContainerType
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.village.VillagerTradesEvent
import net.minecraftforge.event.village.WandererTradesEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.resource.IResourceType
import net.minecraftforge.resource.ISelectiveResourceReloadListener
import java.util.function.Predicate
import java.util.function.Supplier
import kotlin.Exception
import kotlin.math.min

@Mod.EventBusSubscriber
object SetupLifecycle {

    init {
        BountifulMod.logger.info("Loading Bountiful listeners..")
    }

    class BountifulLoadingException(reason: String) : Exception("Bountiful failed to load JSON data. Reason: $reason")

    @SubscribeEvent
    fun gameSetup(event: FMLCommonSetupEvent) {
        println("Registering data type adapters for JSON/Data conversion...")
        JsonSerializers.register()
    }


    fun validatePool(pool: IEntryPool, sender: CommandSource? = null, log: Boolean = false): MutableList<BountyEntry> {

        BountifulMod.logger.info("Validating pool on side? isRemote?: ${sender?.world?.isRemote}")

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
            val bountyStacks = player.inventory.mainInventory.filter { it.item is ItemBounty && it.hasTag() }
            if (bountyStacks.isNotEmpty()) {
                bountyStacks.forEach { stack ->
                    val data = BountyData.from(stack)
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
    }


    @SubscribeEvent
    fun onServerAboutToStart(event: FMLServerAboutToStartEvent) {
        BountifulMod.logger.info("Bountiful listening for resource reloads..")
        event.server.resourceManager.addReloadListener(object : IResourceManagerReloadListener {
            override fun onResourceManagerReload(resourceManager: IResourceManager) {
                BountifulMod.logger.info("Bountiful reloading resources! :D")
                BountifulResourceType.values().forEach { type ->
                    BountifulMod.reloadBountyData(event.server, resourceManager, type)
                }
            }
        })
    }

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {
        BountifulCommand.generateCommand(event.commandDispatcher)
    }

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        println("Registering to: ${event.registry.registryName}, ${event.registry.registrySuperType}")
        event.registry.registerAll(
                ModContent.Items.BOUNTY,
                ModContent.Items.BOUNTYBOARD,
                ModContent.Items.DECREE
        )
    }

    @SubscribeEvent
    fun registerBlocks(event: RegistryEvent.Register<Block>) {
        println("Registering to: ${event.registry.registryName}, ${event.registry.registrySuperType}")
        event.registry.registerAll(
                ModContent.Blocks.BOUNTYBOARD
        )
    }

    @SubscribeEvent
    fun onTileEntityRegistry(event: RegistryEvent.Register<TileEntityType<*>>) {
        event.registry.register(
                TileEntityType.Builder.create<BoardTE>(Supplier {
                    BoardTE()
                }, ModContent.Blocks.BOUNTYBOARD)
                        .build(null)
                        .setRegistryName("${BountifulMod.MODID}:bounty-te")
        )
    }

    @SubscribeEvent
    fun onContainerRegistry(event: RegistryEvent.Register<ContainerType<*>>) {
        event.registry.register(IForgeContainerType.create { windowId, inv, data ->
            val pos = data.readBlockPos()
            BoardContainer(windowId, inv.player.world, pos, inv)
        }.setRegistryName("bountyboard"))
    }

    @SubscribeEvent
    fun onClientInit(event: FMLClientSetupEvent) {
        ScreenManager.registerFactory(ModContent.Guis.BOARDCONTAINER) {
            container, inv, textComponent ->  BoardScreen(container, inv, textComponent)
        }
    }

    private val decreeTrade = BasicTrade(3, ItemStack(ModContent.Items.DECREE), 5, 5, 0.5f)

    @SubscribeEvent
    fun doWandererTrades(event: WandererTradesEvent) {
        event.genericTrades.add(decreeTrade)
    }

    @SubscribeEvent
    fun doVillagerTrades(event: VillagerTradesEvent) {
        event.trades[2].add(decreeTrade)
    }

}


