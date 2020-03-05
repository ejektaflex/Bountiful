package ejektaflex.bountiful.data

import ejektaflex.bountiful.generic.IIdentifiable

interface IDecree : IIdentifiable {
    val decreeTitle: String
    val isGreedy: Boolean
    val spawnsInBoard: Boolean
    val objectivePools: MutableList<String>
    val rewardPools: MutableList<String>
    //val objectives: List<BountyEntry>
    //val rewards: List<BountyEntry>
}