package io.ejekta.bountiful.common.bounty.data.pool

data class Decree(
    val id: String = "DEFAULT_DECREE",
    val objectivePools: MutableSet<Pool>,
    val rewardPools: MutableSet<Pool>
    ) {

}