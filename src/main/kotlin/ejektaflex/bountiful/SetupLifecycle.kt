package ejektaflex.bountiful

import ejektaflex.bountiful.api.data.json.JsonAdapter
import ejektaflex.bountiful.api.data.json.JsonSerializers
import ejektaflex.bountiful.data.Decree
import ejektaflex.bountiful.data.DefaultData
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import java.io.File

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
    fun registerItems(event: RegistryEvent.Register<Item>) {

        val bountyItem = ItemBounty(
                Item.Properties().maxStackSize(1)
        ).apply {
            setRegistryName("bountiful", "bounty")
        }
        println("Registering to: ${event.registry.registryName}, ${event.registry.registrySuperType}")
        event.registry.register(bountyItem)

    }

    @SubscribeEvent
    fun registerModels(event: ModelRegistryEvent) {
        BountifulMod.logger.info("Registering Bountiful models..")
        /*
        ContentRegistry.items.forEach {
            if (it is ItemBounty) {
                registerBountyRenderer(it, 0, it.registryName!!.path)
            } else {
                registerItemRenderer(it, 0, it.registryName!!.path)
            }
        }
        */
    }

    /*
    @SubscribeEvent
    fun onConfigChange(event: ConfigChangedEvent.OnConfigChangedEvent) {
        if (event.modID == "bountiful") {
            BountifulConfig.Client.get()
            BountifulConfig.Common.get()
        }
    }
     */


}


