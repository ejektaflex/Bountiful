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
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.minecraft.block.Block
import net.minecraft.client.gui.ScreenManager
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.Item
import net.minecraft.resources.IResourceManager
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.common.extensions.IForgeContainerType
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
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
        setupConfig()

    }


    fun validatePool(pool: IEntryPool, sender: CommandSource? = null, log: Boolean = false): MutableList<BountyEntry> {

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

    fun loadContentFromFiles(sender: CommandSource? = null) {
        BountifulMod.logger.warn("Loading content from files (client? ${sender})")


        BountifulMod.logger.apply {
            info("Reloading content from files..")


            val decreeBackup = DecreeRegistry.backup()
            val poolBackup = PoolRegistry.backup()

            DecreeRegistry.empty()
            PoolRegistry.empty()

            var succ = true

            for (file in BountifulMod.configDecrees.listFiles()!!.filter { it.extension == "json" }) {
                info("Found DECREEFILE ${file.name}")
                try {
                    val fileText = file.readText()
                    val decree = JsonAdapter.fromJson<Decree>(fileText)
                    DecreeRegistry.add(decree)
                } catch (e: Exception) {
                    sender?.sendErrorMsg("Could not load decree in file ${file.name}. Details: ${e.message}")
                    succ = false
                    DecreeRegistry.restore(decreeBackup)
                    PoolRegistry.restore(poolBackup)
                }
            }

            for (file in BountifulMod.configPools.listFiles()!!.filter { it.extension == "json" }) {
                info("Found POOLFILE ${file.name}")
                try {
                    val fileText = file.readText()
                    val pool = JsonAdapter.fromJson<EntryPool>(fileText)

                    // Run entries through validation
                    val entries = validatePool(pool, sender, false)
                    pool.restore(entries)

                    // Add pool with only valid entries
                    PoolRegistry.add(pool)

                } catch (e: Exception) {
                    sender?.sendErrorMsg("Could not load pool in file '${file.name}'. Details: ${e.message}")
                    succ = false
                    DecreeRegistry.restore(decreeBackup)
                    PoolRegistry.restore(poolBackup)
                }
            }

            if (succ) {
                sender?.sendMessage("Bounty data reloaded.")
            } else {
                sender?.sendErrorMsg("Reverting to previous safe data.")
            }

            info("Testing done.")
        }

    }

    private fun setupConfig() = BountifulConfig.register()

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
    fun onConfigChange(event: ConfigChangedEvent.OnConfigChangedEvent) {
        if (event.modID == BountifulMod.MODID) {
            BountifulConfig.Client.get()
            BountifulConfig.Common.get()
        }
    }


    @SubscribeEvent
    fun onServerAboutToStart(event: FMLServerAboutToStartEvent) {
        BountifulMod.logger.info("Bountiful listening for resource reloads..")
        event.server.resourceManager.addReloadListener(object : ISelectiveResourceReloadListener {
            override fun onResourceManagerReload(resourceManager: IResourceManager, resourcePredicate: Predicate<IResourceType>) {
                BountifulMod.logger.info("Bountiful reloading resources! :D")
                BountifulResourceType.values().forEach { type ->
                    if (resourcePredicate.test(type)) {
                        // doot
                    }
                    // always running it for now
                    BountifulMod.reloadBountyData(event.server, resourceManager, type)
                }
            }
        })
    }

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {
        BountifulCommand.generateCommand(event.commandDispatcher)

        //BountifulMod.logger.info("Bountiful reloading resources on server!")

        //BountifulResourceType.values().forEach { type ->
        //    BountifulMod.tryFillDefaultData(event.server.resourceManager, type)
        //}

        //loadContentFromFiles()


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



        //loadContentFromFiles()

    }

}


