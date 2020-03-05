package ejektaflex.bountiful

import net.minecraft.stats.IStatFormatter
import net.minecraft.stats.Stats
import net.minecraft.util.ResourceLocation
import net.minecraft.util.registry.Registry

object BountifulStats {

    fun init() {
        // can be empty..
    }

    val BOUNTIES_TAKEN = registerCustom("bounties_taken", IStatFormatter.DEFAULT)
    val BOUNTIES_DONE = registerCustom("bounties_done", IStatFormatter.DEFAULT)

    val BOUNTIES_DONE_COMMON = registerCustom("bounties_done_0", IStatFormatter.DEFAULT)
    val BOUNTIES_DONE_UNCOMMON = registerCustom("bounties_done_1", IStatFormatter.DEFAULT)
    val BOUNTIES_DONE_RARE = registerCustom("bounties_done_2", IStatFormatter.DEFAULT)
    val BOUNTIES_DONE_EPIC = registerCustom("bounties_done_3", IStatFormatter.DEFAULT)

    private fun registerCustom(key: String, frm: IStatFormatter): ResourceLocation {
        val resourcelocation = ResourceLocation(BountifulMod.MODID, key)
        Registry.register(Registry.CUSTOM_STAT, key, resourcelocation)
        Stats.CUSTOM.get(resourcelocation, frm)
        return resourcelocation
    }

}