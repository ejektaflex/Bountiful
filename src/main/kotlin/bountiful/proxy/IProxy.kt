package bountiful.proxy

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

interface IProxy {
    fun preInit(e: FMLPreInitializationEvent) {
        //Bountiful.logger = e.modLog
        //Bountiful.proxy.preInit(e)
    }

    fun init(e: FMLInitializationEvent) {
        //Bountiful.proxy.init(e)
    }

    fun postInit(e: FMLPostInitializationEvent) {
        //Bountiful.proxy.postInit(e)
    }
}