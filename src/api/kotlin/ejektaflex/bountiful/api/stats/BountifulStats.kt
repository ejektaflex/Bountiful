package ejektaflex.bountiful.api.stats

import net.minecraft.stats.StatBasic
import net.minecraft.util.text.TextComponentTranslation

/**
 * A singleton containing all Statistics pertaining to Bountiful.
 */
object BountifulStats {

    open class BountifulStat(statRoot: String) : StatBasic("stat.$statRoot", TextComponentTranslation("bountiful.stat.$statRoot"))

    //val bountiesTaken = BountifulStat("bountiesTaken")
    val bountiesCompleted = BountifulStat("bountiesCompleted")
    val bountiesCommon = BountifulStat("bountiesCommon")
    val bountiesUncommon = BountifulStat("bountiesUncommon")
    val bountiesRare = BountifulStat("bountiesRare")
    val bountiesEpic = BountifulStat("bountiesEpic")

    fun register(): Boolean {
        listOf(
                //bountiesTaken,
                bountiesCompleted,
                bountiesCommon,
                bountiesUncommon,
                bountiesRare,
                bountiesEpic
        ).forEach {
            it.registerStat()
        }

        return true
    }


}