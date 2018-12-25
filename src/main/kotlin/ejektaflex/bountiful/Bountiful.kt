package ejektaflex.bountiful

import ejektaflex.bountiful.api.BountifulAPIProvider
import ejektaflex.bountiful.command.BountyCommand
import ejektaflex.bountiful.config.BountifulIO
import ejektaflex.bountiful.config.ConfigFile
import ejektaflex.bountiful.proxy.IProxy
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.Logger
import net.minecraftforge.fml.common.SidedProxy
import ejektaflex.bountiful.gui.GuiHandler
import net.minecraftforge.fml.common.event.*
import net.minecraftforge.fml.common.network.NetworkRegistry
import java.io.File


@Mod(modid = BountifulInfo.MODID, name = BountifulInfo.NAME, version = BountifulInfo.VERSION, modLanguageAdapter = BountifulInfo.ADAPTER, dependencies = BountifulInfo.DEPENDS)
object Bountiful : IProxy {

    @SidedProxy(clientSide = BountifulInfo.CLIENT, serverSide = BountifulInfo.SERVER)
    @JvmStatic lateinit var proxy: IProxy

    @Mod.EventHandler
    fun loadAPI(event: FMLConstructionEvent) = BountifulAPIProvider.changeAPI(InternalAPI)

    lateinit var logger: Logger
    lateinit var configDir: File
    lateinit var config: ConfigFile

    @Mod.Instance
    var instance: Bountiful? = this

    @Mod.EventHandler
    override fun preInit(e: FMLPreInitializationEvent) {
        logger = e.modLog
        configDir = BountifulIO.ensureDirectory(e.modConfigurationDirectory, BountifulInfo.MODID)
        config = ConfigFile(configDir)
        config.load()
        MinecraftForge.EVENT_BUS.register(ContentRegistry)
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, GuiHandler())
        MinecraftForge.EVENT_BUS.register(proxy)
        proxy.preInit(e)
    }

    @Mod.EventHandler
    override fun init(e: FMLInitializationEvent) {
        proxy.init(e)
    }

    @Mod.EventHandler
    override fun postInit(e: FMLPostInitializationEvent) {
        proxy.postInit(e)
        if (config.hasChanged()) {
            config.save()
        }
    }

    @Mod.EventHandler
    fun serverLoad(e: FMLServerStartingEvent) = e.registerServerCommand(BountyCommand())

}
