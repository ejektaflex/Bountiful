package ejektaflex.bountiful.api.data

import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.api.generic.IIdentifiable

interface IDecree : IIdentifiable {
    val decreeTitle: String
    val isGreedy: Boolean
    val spawnsInBoard: Boolean
    val objectivePools: MutableList<String>
    val rewardPools: MutableList<String>
    //val objectives: List<BountyEntry>
    //val rewards: List<BountyEntry>
}