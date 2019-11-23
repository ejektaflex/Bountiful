package ejektaflex.bountiful

import ejektaflex.bountiful.api.data.json.JsonAdapter
import ejektaflex.bountiful.api.data.json.JsonSerializers
import ejektaflex.bountiful.data.Decree
import ejektaflex.bountiful.data.DefaultData
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import java.io.File

@KotlinEventBusSubscriber
object SetupLifecycle {

    init {
        BountifulMod.logger.info("Loading Bountiful listeners..")
    }

    @SubscribeEvent
    fun testStuff(event: FMLCommonSetupEvent) {

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

            val decreeList = JsonAdapter.fromJson<Array<Decree>>(testFile.readText())
            //DecreeRegistry.restore(decreeList)
            val first = decreeList.first()

            println(first)
            println(first.objectives)
            println(first.rewards)

            info("Testing done.")
        }

    }

    @SubscribeEvent
    fun setupConfig(event: FMLCommonSetupEvent) = BountifulConfig.register()

    @SubscribeEvent
    fun populateDataRegistries(event: FMLCommonSetupEvent) {

    }

    /*
    @SubscribeEvent
    fun dumpDecrees(event: FMLCommonSetupEvent) {
        BountifulMod.logger.info("Bountiful decrees:")
        println(DecreeRegistry.content.first().decreeDescription)
        println("Doot")
        for (decree in DecreeRegistry.content) {
            BountifulMod.logger.info(decree.decreeTitle)
        }
        println("Doot 2")
        for (decree in DecreeRegistry) {
            BountifulMod.logger.info(decree.decreeDescription)
        }
    }
    */

    /*
    fun registerItems(event: RegistryEvent.Register<*>) {

        var regType = event.getRegistry().getRegistrySuperType()

         when (regType) {
            is Item -> {
                val bountyItem = ItemBounty(
                        Item.Properties().maxStackSize(1)
                ).apply {
                    setRegistryName("bountiful", "bounty")
                }
                println("Registering to: ${event.getRegistry().getRegistryName()}, ${event.getRegistry().getRegistrySuperType()}")
                (event.getRegistry() as IForgeRegistry<Item>).register(bountyItem)
            }
        }

    }

     */

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {

        val bountyItem = ItemBounty(
                Item.Properties().maxStackSize(1)
        ).apply {
            setRegistryName("bountiful", "bounty")
        }
        println("Registering to: ${event.getRegistry().getRegistryName()}, ${event.getRegistry().getRegistrySuperType()}")
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

    @SubscribeEvent
    fun onConfigChange(event: ConfigChangedEvent.OnConfigChangedEvent) {
        if (event.modID == "examplemod") {
            BountifulConfig.Client.get()
            BountifulConfig.Common.get()
        }
    }


}


