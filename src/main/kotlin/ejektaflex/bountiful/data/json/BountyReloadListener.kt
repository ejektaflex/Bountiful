package ejektaflex.bountiful.data.json

import com.google.gson.JsonElement
import net.minecraft.client.resources.JsonReloadListener
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation

class BountyReloadListener : JsonReloadListener(null, null) {

    override fun apply(
        objectIn: MutableMap<ResourceLocation, JsonElement>,
        resourceManagerIn: IResourceManager,
        profilerIn: IProfiler
    ) {
        TODO("Not yet implemented")
    }


}