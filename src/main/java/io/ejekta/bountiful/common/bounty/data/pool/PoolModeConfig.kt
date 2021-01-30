package io.ejekta.bountiful.common.bounty.data.pool

class PoolModeConfig(
    val priority: PoolPriority = PoolPriority.CONFIG,
    val overrides: MutableMap<PoolPriority, PoolMode> = mutableMapOf()
) {

}