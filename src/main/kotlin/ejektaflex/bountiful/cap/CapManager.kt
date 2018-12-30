package ejektaflex.bountiful.cap

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject

object CapManager {
    @CapabilityInject(IGlobalBoard::class)
    val CAP_BOARD: Capability<IGlobalBoard>? = null
}