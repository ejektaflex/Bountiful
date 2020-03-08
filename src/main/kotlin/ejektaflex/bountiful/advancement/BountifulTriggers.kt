package ejektaflex.bountiful.advancement

import ejektaflex.bountiful.BountifulMod
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.util.ResourceLocation

object BountifulTriggers {

    val BOUNTY_TAKEN = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "bounty_taken"))

    val COMPLETE_STARTER = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "bounty_complete"))

    val RUSH_ORDER = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "rush_order"))

    val PROCRASTINATOR = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "procrastinator"))

    val TOWN_CRIER = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "town_crier"))

    //val ROYAL_MANDATE = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "royal_mandate"))

    //val COMPLETE_COMMON = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "bounty_complete_common"))
    //val COMPLETE_UNCOMMON = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "bounty_complete_uncommon"))
    //val COMPLETE_RARE = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "bounty_complete_rare"))
    //val COMPLETE_EPIC = BountifulTrigger(ResourceLocation(BountifulMod.MODID, "bounty_complete_epic"))

    val inListForm: List<BountifulTrigger> by lazy {
        listOf(
                BOUNTY_TAKEN,
                COMPLETE_STARTER,
                RUSH_ORDER,
                TOWN_CRIER,
                //ROYAL_MANDATE,
                PROCRASTINATOR
                //COMPLETE_COMMON,
                //COMPLETE_UNCOMMON,
                //COMPLETE_RARE,
                //COMPLETE_EPIC

        )
    }

    fun register() {
        inListForm.forEach { trigger ->
            CriteriaTriggers.register(trigger)
        }
    }

}