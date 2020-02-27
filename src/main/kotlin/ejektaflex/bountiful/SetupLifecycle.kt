package ejektaflex.bountiful

import ejektaflex.bountiful.api.data.json.JsonAdapter
import ejektaflex.bountiful.api.data.json.JsonSerializers
import ejektaflex.bountiful.api.ext.sendErrorMsg
import ejektaflex.bountiful.api.ext.sendMessage
import ejektaflex.bountiful.block.BoardTE
import ejektaflex.bountiful.command.BountifulCommand
import ejektaflex.bountiful.content.ModContent
import ejektaflex.bountiful.data.Decree
import ejektaflex.bountiful.data.DefaultData
import ejektaflex.bountiful.data.EntryPool
import ejektaflex.bountiful.gui.BoardContainer
import ejektaflex.bountiful.gui.BoardScreen
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.minecraft.block.Block
import net.minecraft.client.gui.ScreenManager
import net.minecraft.command.CommandSource
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.common.extensions.IForgeContainerType
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import java.util.function.Supplier
import kotlin.Exception

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
        exportContent() // TODO NOT overwrite content
        clearContent()
        loadContentFromFiles()
        dumpDecrees()
    }

    private fun exportContent() {
        println("Dumping default data into registries..")
        DefaultData.export()
    }

    private fun clearContent() {
        DecreeRegistry.empty()
        PoolRegistry.empty()
    }


    fun loadContentFromFiles(sender: CommandSource? = null) {

        BountifulMod.logger.apply {

            val decreeBackup = DecreeRegistry.backup()
            val poolBackup = PoolRegistry.backup()

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
                    PoolRegistry.add(pool)
                } catch (e: Exception) {
                    sender?.sendErrorMsg("Could not load pool in file '${file.name}'. Details: ${e.message}")
                    succ = false
                    DecreeRegistry.restore(decreeBackup)
                    PoolRegistry.restore(poolBackup)
                }
            }

            if (succ) {
                sender?.sendMessage("Bounty data reloaded successfully!")
            } else {
                sender?.sendErrorMsg("Reverting to previous safe data.")
            }

            info("Testing done.")
        }

    }

    private fun setupConfig() = BountifulConfig.register()

    private fun dumpDecrees() {
        BountifulMod.logger.info("Bountiful decrees:")
        println(DecreeRegistry.content.first().decreeDescription)
        println("Doot")
        println("Doot 2")
        for (decree in DecreeRegistry) {
            BountifulMod.logger.info(decree.decreeTitle)
            BountifulMod.logger.info(decree.decreeDescription)
        }
    }

    @SubscribeEvent
    fun onConfigChange(event: ConfigChangedEvent.OnConfigChangedEvent) {
        if (event.modID == "bountiful") {
            BountifulConfig.Client.get()
            BountifulConfig.Common.get()
        }
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
        println("BOUQ registering tile entities")

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

}


