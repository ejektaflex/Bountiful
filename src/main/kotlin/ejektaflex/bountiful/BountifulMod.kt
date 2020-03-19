package ejektaflex.bountiful

import ejektaflex.bountiful.data.bounty.enums.BountifulResourceType
import ejektaflex.bountiful.data.json.JsonAdapter
import ejektaflex.bountiful.data.structure.EntryPool
import ejektaflex.bountiful.ext.sendErrorMsg
import ejektaflex.bountiful.network.BountifulNetwork
import ejektaflex.bountiful.util.IMerge
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
import java.io.File
import java.nio.file.Paths


@Mod(BountifulMod.MODID)
object BountifulMod {

    const val MODID = "bountiful"

    val logger: Logger = LogManager.getLogger()

    const val VERSION = "3.1.0"

    init {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BountifulConfig.serverSpec)
        BountifulNetwork.register()
    }

    val logFolder = Paths.get("logs").toFile().apply {
        mkdirs()
    }

    val logFile = File(logFolder, "bountiful.log").apply {
        if (exists()) {
            delete()
        }
        createNewFile()
    }

    fun rlFileName(rl: ResourceLocation) = rl.path.substringAfterLast("/")

    fun rlFileNameNoExt(rl: ResourceLocation) = rlFileName(rl).substringBefore(".json")

    fun loadResource(manager: IResourceManager, msgSender: CommandSource?, location: BountifulResource, fillType: BountifulResourceType): IMerge<Any>? {

        val res = manager.getResource(location.rl)
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
        newObj.id = rlFileNameNoExt(location.rl)

        return newObj
    }

    data class BountifulResource(val rl: ResourceLocation, val type: BountifulResourceType) {

        val originPack: String by lazy {
            rl.namespace
        }

        val originCompat: String by lazy {
            rl.path.substringAfter("bounties/${type.folderName}/").substringBefore(rlFileName(rl)).dropLast(1)
        }

        val origin: String by lazy {
            "$originPack/$originCompat"
        }

        val typedOrigin: String by lazy {
            "$originPack/${type.folderName}/$originCompat"
        }

        override fun toString(): String {
            return "BR{$origin}"
        }

        fun isBlacklisted(blacklist: List<String>): Boolean {
            val blacklistRegexes = blacklist.map {
                it.replace("*", "([a-zA-Z0-9\\-_.]+)").toRegex()
            }

            return blacklistRegexes.any { it.matches(typedOrigin) }
        }

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

            //logger.error("########## FILENAME: $filename ##########")

            //logger.error("Locs: ${locations.map { it.toString() }}")

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
            }.map { list ->
                list.map { BountifulResource(it, fillType) }
            }


            //logger.warn("Compatloadableresources: ${compatLoadableResources.map { it.toString() }}")

            groupLoop@ for (validPathList in compatLoadableResources) {

                val locationToLoad = validPathList.last()

                val blacklist = BountifulConfig.SERVER.blacklistedData.get()

                if (locationToLoad.isBlacklisted(blacklist)) {
                    logger.warn("Bountiful location blacklisted by user: $locationToLoad")
                    continue@groupLoop
                }

                val newObj = loadResource(manager, msgSender, locationToLoad, fillType)

                if (newObj != null) {
                    if (newObj.canLoad) {
                        //logger.warn("Is about to load/set $locationToLoad")
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
