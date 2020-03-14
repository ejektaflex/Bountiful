package ejektaflex.bountiful

import ejektaflex.bountiful.util.IMerge
import ejektaflex.bountiful.data.json.JsonAdapter
import ejektaflex.bountiful.ext.sendErrorMsg
import ejektaflex.bountiful.data.bounty.enums.BountifulResourceType
import ejektaflex.bountiful.data.structure.EntryPool
import ejektaflex.bountiful.util.ValueRegistry
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
import java.lang.Exception


@Mod(BountifulMod.MODID)
object BountifulMod {

    const val MODID = "bountiful"

    val logger: Logger = LogManager.getLogger()

    init {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BountifulConfig.serverSpec)
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

            val filenameNoExtension = filename.substringBefore(".json")

            var obj: IMerge<Any>? = null

            logger.error("########## FILENAME: $filename ##########")

            logger.error("Locs: ${locations.map{ it.toString() }}")

            // Go through each namespace in order
            nameLoop@ for (namespace in manager.resourceNamespaces - BountifulConfig.SERVER.namespaceBlacklist.get()) {

                logger.warn("Inspecting namespace: $namespace")

                // Try get the RL of the namespace for this file
                val location = locations.find { it.namespace == namespace }

                logger.info("- Location found? $location (${location?.path})")

                if (location != null ) {
                    logger.info("- - Yes!")

                    val res = manager.getResource(location)
                    val content = res.inputStream.reader().readText()

                    val newObj = try {
                        JsonAdapter.fromJsonExp(content, fillType.klazz)
                    } catch (e: Exception) {
                        msgSender?.sendErrorMsg("Skipping resource $location. Reason: ${e.message}")
                        continue@nameLoop
                    } as IMerge<Any>

                    // Set ID to filename
                    newObj.id = filenameNoExtension

                    //logger.info("New obj is: $newObj")

                    if (newObj.canLoad) {
                        if (obj != null) {
                            //logger.warn("MERGING $obj with $newObj")

                            obj.merge(newObj)
                            //logger.warn("RESULT IS: $obj")
                        } else {
                            obj = newObj
                        }
                    }

                }

            }

            // Adding item to pool
            if (obj != null) {

                if (obj is EntryPool) {
                    SetupLifecycle.validatePool(obj, msgSender)
                }

                (fillType.reg as ValueRegistry<Any>).add(obj as Any)
            }

        }

    }

    init {
        FMLKotlinModLoadingContext.get().modEventBus.register(SetupLifecycle)
    }

    val config = BountifulConfig()

}
