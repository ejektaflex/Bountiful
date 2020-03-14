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


    fun rlFileName(rl: ResourceLocation) = rl.path.substringAfterLast("/")

    fun rlFileNameNoExt(rl: ResourceLocation) = rlFileName(rl).substringBefore(".json")

    fun loadResource(manager: IResourceManager, msgSender: CommandSource?, location: ResourceLocation, fillType: BountifulResourceType): IMerge<Any>? {

        val res = manager.getResource(location)
        val content = res.inputStream.reader().readText()

        val newObj = try {
            logger.info("Loading $location")
            JsonAdapter.fromJsonExp(content, fillType.klazz)
        } catch (e: Exception) {
            logger.info("CANNOT LOAD JSON at $location. Reason: ${e.message}")
            msgSender?.sendErrorMsg("Skipping resource $location. Reason: ${e.message}")
            return null
        } as IMerge<Any>

        // Set ID to filename
        newObj.id = rlFileNameNoExt(location)

        return newObj
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

        // Get all resource locations, grouped by namespace
        val spaceMap = manager.getAllResourceLocations(folderName) {
            it.endsWith(extension)
        }.groupBy { rl -> rlFileName(rl) }

        // For each group of files with the same name
        fileLoop@ for ((filename, locations) in spaceMap) {

            var obj: IMerge<Any>? = null

            logger.error("########## FILENAME: $filename ##########")

            logger.error("Locs: ${locations.map { it.toString() }}")

            val spaceList = manager.resourceNamespaces.toList()

            val compatLoadableResources = spaceList.map {
                "$folderName/$it/$filename"
            }.map {
                listOf(locations.filter { loc -> loc.path == it })
                        .sortedBy {
                            spaceList.indexOf(it.first().namespace)
                        }.flatten()
            }.filter {
                it.isNotEmpty()
            }

            logger.warn("Compatloadableresources: ${compatLoadableResources.map { it.toString() }}")

            groupLoop@ for (validPathList in compatLoadableResources) {

                val locationToLoad = validPathList.last()

                val newObj = loadResource(manager, msgSender, locationToLoad, fillType)

                if (newObj != null) {
                    if (newObj.canLoad) {
                        logger.warn("Is about to load/set $locationToLoad")
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
