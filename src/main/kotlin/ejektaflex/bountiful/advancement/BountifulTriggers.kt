package ejektaflex.bountiful.advancement

import ejektaflex.bountiful.BountifulMod
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.util.ResourceLocation

object BountifulTriggers {

    val BOUNTY_TAKEN = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "bounty_taken"))
    
    val COMPLETE_COMMON = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "bounty_complete_common"))
    val COMPLETE_UNCOMMON = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "bounty_complete_uncommon"))
    val COMPLETE_RARE = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "bounty_complete_rare"))
    val COMPLETE_EPIC = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "bounty_complete_epic"))

    val inListForm: List<BountifulTrigger>
        get() = listOf(
                BOUNTY_TAKEN,
                COMPLETE_COMMON,
                COMPLETE_UNCOMMON,
                COMPLETE_RARE,
                COMPLETE_EPIC
        )

    fun register() {
        inListForm.forEach { trigger ->
            CriteriaTriggers.register(trigger)
        }
    }

}