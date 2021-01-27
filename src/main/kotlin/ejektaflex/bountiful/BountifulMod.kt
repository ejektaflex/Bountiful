package ejektaflex.bountiful

import ejektaflex.bountiful.network.BountifulNetwork
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

    val config = BountifulConfig()

}
