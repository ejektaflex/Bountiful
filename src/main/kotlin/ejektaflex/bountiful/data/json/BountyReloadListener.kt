package ejektaflex.bountiful.data.json

import com.google.gson.JsonElement
import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.SetupLifecycle
import ejektaflex.bountiful.data.bounty.enums.BountifulResourceType
import ejektaflex.bountiful.data.registry.DecreeRegistry
import ejektaflex.bountiful.data.registry.PoolRegistry
import ejektaflex.bountiful.data.structure.Decree
import ejektaflex.bountiful.data.structure.EntryPool
import net.minecraft.client.resources.JsonReloadListener
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IResourceManager
import net.minecraft.resources.ResourceLocation

class BountyReloadListener : JsonReloadListener(JsonAdapter.gson, "bounties") {

    override fun apply(
        objectIn: MutableMap<ResourceLocation, JsonElement>,
        resourceManagerIn: IResourceManager,
        profilerIn: IProfiler
    ) {

        val decreeMap = mutableMapOf<String, MutableList<Decree>>()
        val poolMap = mutableMapOf<String, MutableList<EntryPool>>()

        BountifulMod.logger.info("Loading Bounty Data. We will probably skip some data since certain mods are not loaded.")

        for ((rl, element) in objectIn) {
            if (rl.path.startsWith("_")) { continue }

            //BountifulMod.logger.info("Loading resource at $rl.")

            val toGrab = rl.path.substringBefore('/')

            val typeOfFile = BountifulResourceType.values().find { it.folderName == toGrab } ?: continue // ignore non resource types!

            val loaded = JsonAdapter.fromJson(element, typeOfFile.klazz)

            when (loaded) {
                is Decree -> {
                    decreeMap.getOrPut(rl.path.substringAfter('/')) { mutableListOf() }.add(loaded.also { it.id =
                        BountifulMod.rlFileNameNoExt(rl)
                    })
                }
                is EntryPool -> {
                    if (!loaded.canLoad) {
                        BountifulMod.logger.info("Skipping load of $rl as dependencies are not met: ${loaded.modsRequired}")
                        continue
                    }
                    poolMap.getOrPut(
                        rl.path
                            .substringAfter('/')
                            .substringAfter('/')
                    ) { mutableListOf() }.add(loaded.also { it.id =
                        BountifulMod.rlFileNameNoExt(rl)
                    })
                }
            }
        }

        // Merge decrees and pools based on paths

        val decreesMapped = decreeMap.map { entry -> entry.key to entry.value.reduce { a, b ->
            if (b.canLoad) a.merge(b) else a
        } }.toMap().values.toList()

        DecreeRegistry.restore(decreesMapped)

        BountifulMod.logger.info("Found decrees: ${DecreeRegistry.ids}")

        BountifulMod.logger.info("Merging Entry Pools..")


        val poolsMapped = poolMap.map { entry -> entry.key to entry.value.reduce { a, b ->
            if (b.canLoad) a.merge(b) else a.also {
                BountifulMod.logger.info("Not merging ${b.id} which requires ${b.modsRequired}")
            }
        } }.toMap().values.toList()


        BountifulMod.logger.info("Validating Entry Pools..")

        poolsMapped.forEach {
            BountifulMod.logger.info("Validating pool '${it.id}'")
            val invalid = SetupLifecycle.validatePool(it, null, false)
            for (item in invalid) {
                BountifulMod.logger.warn("Invalid pool item: $item")
            }
        }

        PoolRegistry.restore(poolsMapped)

        BountifulMod.logger.info("Found Entry Pools: ${PoolRegistry.ids}")

    }

}