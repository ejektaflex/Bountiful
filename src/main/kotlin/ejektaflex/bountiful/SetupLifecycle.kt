package ejektaflex.bountiful

import ejektaflex.bountiful.api.data.json.JsonAdapter
import ejektaflex.bountiful.api.data.json.JsonSerializers
import ejektaflex.bountiful.block.BoardTE
import ejektaflex.bountiful.content.ModContent
import ejektaflex.bountiful.data.Decree
import ejektaflex.bountiful.data.DefaultData
import ejektaflex.bountiful.gui.BoardContainer
import ejektaflex.bountiful.gui.BoardScreen
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.minecraft.block.Block
import net.minecraft.client.gui.ScreenManager
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
import java.io.File
import java.util.function.Supplier

@Mod.EventBusSubscriber
object SetupLifecycle {

    init {
        BountifulMod.logger.info("Loading Bountiful listeners..")
    }

    @SubscribeEvent
    fun gameSetup(event: FMLCommonSetupEvent) {
        setupConfig()
        loadContent()
        dumpDecrees()
    }

    fun loadContent() {

        BountifulMod.logger.apply {
            info("Dumping default data into registries..")

            DefaultData.export()

            info("Registering data type adapters for JSON/Data conversion...")
            JsonSerializers.register()

            info("Creating file to dump data...")
            val testFile = File("test.json")
            testFile.writeText(JsonAdapter.toJson(
                    DefaultData.decrees.content
            ))

            DecreeRegistry.restore(DefaultData.decrees.content)
            PoolRegistry.restore(DefaultData.pools.content)

            info(testFile.readText())

            val decreeList = JsonAdapter.fromJson<Array<Decree>>(testFile.readText()).toList()
            DecreeRegistry.restore(decreeList)
            val first = decreeList.first()

            println(first)
            //println(first.objectives)
            //println(first.rewards)

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
    fun registerItems(event: RegistryEvent.Register<Item>) {
        println("Registering to: ${event.registry.registryName}, ${event.registry.registrySuperType}")
        event.registry.registerAll(
                ModContent.Items.BOUNTY,
                ModContent.Items.BOUNTYBOARD
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


