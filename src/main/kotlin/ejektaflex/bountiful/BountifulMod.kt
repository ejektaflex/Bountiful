package ejektaflex.bountiful

import ejektaflex.bountiful.api.BountifulAPIProvider
import ejektaflex.bountiful.data.BountifulResourceType
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraft.command.impl.DataPackCommand
import net.minecraft.resources.IResourceManager
import net.minecraft.resources.ResourcePackType
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ResourceLocation
import net.minecraft.world.dimension.DimensionType
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.packs.ResourcePackLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.nio.file.Paths


@Mod("bountiful")
object BountifulMod {

    const val MODID = "bountiful"

    val logger: Logger = LogManager.getLogger()

    val configFolder = Paths.get("config", "bountiful").toFile().apply {
        mkdirs()
    }

    val configDecrees = File(configFolder, "decrees").apply {
        mkdirs()
    }

    val configPools = File(configFolder, "pools").apply {
        mkdirs()
    }

    /*
    fun loadServerPackData(server: MinecraftServer) {
        val packs = server.resourcePacks

        // Using all packs for now, will later want to just do all packs if there's an empty config
        for (packInfo in packs.allPacks) {

            //this.loadDataPacks(this.getWorld(DimensionType.OVERWORLD).getWorldInfo())
            val pack = server.

            logger.info("Troo to grab server data?")
            logger.info("Loca: ${pack.getResourceNamespaces(ResourcePackType.SERVER_DATA)}")
            pack.getAllResourceLocations(ResourcePackType.SERVER_DATA, "decrees", 4) {
                it.endsWith(".json")
            }.forEach { rl ->
                println("RL: $rl")
            }
            logger.info("Grabbed server data.")
        }


    }
     */

    /*
        val pack = ResourcePackLoader.getResourcePackFor(MODID).get()

        logger.info("Tryina to grab server data?")
        logger.info("Loca: ${pack.getResourceNamespaces(ResourcePackType.SERVER_DATA)}")
        pack.getAllResourceLocations(ResourcePackType.SERVER_DATA, "decrees", 4) {
            it.endsWith(".json")
        }.forEach { rl ->
            println("RL: $rl")
        }
        logger.info("Grabbed server data.")
    */

    /*
    if (force) {
        fillType.folderLoc.listFiles()!!.forEach { file ->
            file.delete()
            logger.info("Files deleted")
        }
    }

     */

    fun tryFillDefaultData(server: MinecraftServer, manager: IResourceManager, fillType: BountifulResourceType, force: Boolean = false) {
        logger.info("Copying default data for bounty resource ${fillType.name} (force=$force)")

        val folderName = "bounties/" + fillType.folderName
        val extension = ".json"

        for (packInfo in server.resourcePacks.enabledPacks) {
            val pack = packInfo.resourcePack
            logger.info("Pack: ${packInfo.name}")

            for (loccie in pack.getAllResourceLocations(ResourcePackType.SERVER_DATA, folderName, 4) {
                it.endsWith(".json")
            }) {

                logger.error("PACKRESOURCEFOUND: $loccie")

            }


        }

        DecreeRegistry.empty()
        PoolRegistry.empty()


        logger.warn("Namespaces: ${manager.resourceNamespaces}")

        for (namespace in manager.resourceNamespaces)


        for (fullLocation in manager.getAllResourceLocations(folderName) {
            it.endsWith(extension)
        }) {

            logger.info("Full location: $fullLocation")

            val res = manager.getResource(fullLocation)
            //logger.info("TEXT: ${manager.getResource(fullLocation).inputStream.reader().readText()}")
        }


    }

    init {
        BountifulAPIProvider.changeAPI(BountifulAPIImpl)
        FMLKotlinModLoadingContext.get().modEventBus.register(SetupLifecycle)
    }

    // Temporary dummy config until real config files are replaced
    val config = ConfigDummy()

}
