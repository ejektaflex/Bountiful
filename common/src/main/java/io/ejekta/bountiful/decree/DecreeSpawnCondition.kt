package io.ejekta.bountiful.decree

import io.ejekta.bountiful.data.Decree

enum class DecreeSpawnCondition(val spawnFunc: Decree.() -> Boolean) {
    NONE({ true }),
    BOARD_SPAWN({ canSpawn }),
    BOARD_REVEAL({ canReveal }),
    WANDERING_TRADER({ canWanderBuy })
}