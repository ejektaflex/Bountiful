package ejektaflex.bountiful

import ejektaflex.bountiful.api.BountifulAPIProvider
import ejektaflex.bountiful.api.IMerge
import ejektaflex.bountiful.api.data.json.JsonAdapter
import ejektaflex.bountiful.data.BountifulResourceType
import ejektaflex.bountiful.data.ValueRegistry
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraft.resources.IResourceManager
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Mod
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

    fun reloadBountyData(server: MinecraftServer, manager: IResourceManager = server.resourceManager, fillType: BountifulResourceType, force: Boolean = false) {
        logger.info("Copying default data for bounty resource ${fillType.name} (force=$force)")

        val folderName = "bounties/" + fillType.folderName
        val extension = ".json"

        fillType.reg.empty()

        logger.warn("Namespaces: ${manager.resourceNamespaces}")

        fun rlFileName(rl: ResourceLocation) = rl.path.substringAfter("$folderName/")

        // Get all resource locations, grouped by namespace
        val spaceMap = manager.getAllResourceLocations(folderName) {
            it.endsWith(extension)
        }.groupBy { rl -> rlFileName(rl) }

        // For each group of files with the same name
        for ((filename, locations) in spaceMap) {

            var obj: IMerge<Any>? = null

            logger.error("########## FILENAME: $filename ##########")

            // Go through each namespace in order
            for (namespace in manager.resourceNamespaces - BountifulMod.config.namespaceBlacklist) {

                logger.warn("Inspecting namespace: $namespace")

                // Try get the RL of the namespace for this file
                val location = locations.find { it.namespace == namespace }

                logger.info("- Location found? $location (${location?.path})")

                location?.let {

                    logger.info("- - Yes!")

                    val res = manager.getResource(it)
                    val content = res.inputStream.reader().readText()

                    val newObj = JsonAdapter.fromJsonExp(content, fillType.klazz)

                    logger.info("New obj is: $newObj")

                    if (obj != null) {
                        logger.warn("MERGING $obj with $newObj")
                        obj!!.merge(newObj)
                        logger.warn("RESULT IS: $obj")
                    } else {
                        obj = newObj as IMerge<Any>
                    }


                }


            }

            // Adding item to pool
            if (obj != null) {
                (fillType.reg as ValueRegistry<Any>).add(obj as Any)
                logger.error("Reg Size Is Now: ${fillType.reg.content.size}")
            }

        }

        //logger.info("Full location: $fullLocation")

        //val res = manager.getResource(fullLocation)
        //logger.info("TEXT: ${manager.getResource(fullLocation).inputStream.reader().readText()}")



    }

    init {
        BountifulAPIProvider.changeAPI(BountifulAPIImpl)
        FMLKotlinModLoadingContext.get().modEventBus.register(SetupLifecycle)
    }

    // Temporary dummy config until real config files are replaced
    val config = ConfigDummy()

}
