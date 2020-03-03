package ejektaflex.bountiful

import ejektaflex.bountiful.api.BountifulAPIProvider
import ejektaflex.bountiful.api.IMerge
import ejektaflex.bountiful.api.data.json.JsonAdapter
import ejektaflex.bountiful.api.ext.sendErrorMsg
import ejektaflex.bountiful.data.BountifulResourceType
import ejektaflex.bountiful.data.EntryPool
import ejektaflex.bountiful.data.ValueRegistry
import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraft.command.CommandSource
import net.minecraft.resources.IResourceManager
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.lang.Exception
import java.nio.file.Paths


@Mod("bountiful")
object BountifulMod {

    const val MODID = "bountiful"

    val logger: Logger = LogManager.getLogger()

    init {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BountifulConfig.serverSpec)
    }

    val configFolder = Paths.get("config", "bountiful").toFile().apply {
        mkdirs()
    }

    val configDecrees = File(configFolder, "decrees").apply {
        mkdirs()
    }

    val configPools = File(configFolder, "pools").apply {
        mkdirs()
    }

    fun reloadBountyData(
            server: MinecraftServer,
            manager: IResourceManager = server.resourceManager,
            fillType: BountifulResourceType,
            msgSender: CommandSource? = null
    ) {

        val folderName = "bounties/" + fillType.folderName
        val extension = ".json"

        fillType.reg.empty()

        //logger.warn("Namespaces: ${manager.resourceNamespaces}")

        fun rlFileName(rl: ResourceLocation) = rl.path.substringAfterLast("/")

        // Get all resource locations, grouped by namespace
        val spaceMap = manager.getAllResourceLocations(folderName) {
            it.endsWith(extension)
        }.groupBy { rl -> rlFileName(rl) }

        // For each group of files with the same name
        fileLoop@ for ((filename, locations) in spaceMap) {

            var obj: IMerge<Any>? = null

            //logger.error("########## FILENAME: $filename ##########")

            // Go through each namespace in order
            nameLoop@ for (namespace in manager.resourceNamespaces - config.namespaceBlacklist) {

                //logger.warn("Inspecting namespace: $namespace")

                // Try get the RL of the namespace for this file
                val location = locations.find { it.namespace == namespace }

                //logger.info("- Location found? $location (${location?.path})")

                if (location != null ) {
                    //logger.info("- - Yes!")

                    val res = manager.getResource(location)
                    val content = res.inputStream.reader().readText()

                    val newObj = try {
                        JsonAdapter.fromJsonExp(content, fillType.klazz)
                    } catch (e: Exception) {
                        msgSender?.sendErrorMsg("Skipping resource $location. Reason: ${e.message}")
                        continue@nameLoop
                    }

                    //logger.info("New obj is: $newObj")

                    if (obj != null) {
                        //logger.warn("MERGING $obj with $newObj")
                        obj!!.merge(newObj)
                        //logger.warn("RESULT IS: $obj")
                    } else {
                        obj = newObj as IMerge<Any>
                    }
                }



            }

            // Adding item to pool
            if (obj != null) {

                if (obj is EntryPool) {
                    SetupLifecycle.validatePool(obj, msgSender, true)
                }

                (fillType.reg as ValueRegistry<Any>).add(obj as Any)
                //logger.error("Reg Size Is Now: ${fillType.reg.content.size}")
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
    val config = BountifulConfig()

}
