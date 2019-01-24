package ejektaflex.bountiful.api.data

import ejektaflex.bountiful.api.logic.picked.IPickedEntry

interface IPresetBounty : IWeighted {
    /**
     * A list of strings representing which objectives you must complete in order to complete the bounty, as well as the amount of each
     */
    val objectives: MutableList<PresetBounty.BountyObjective>

    /**
     * A list of strings representing stacks which are the rewards completing the bounty.
     */
    val rewards: MutableList<PresetBounty.BountyReward>

    /**
     * The weight for how often this bounty should be chosen out of the list of preset bounties.
     */
    override var weight: Int

    /**
     * Returns true if the given preset bounty is valid. If not, it cannot be turned into a bounty.
     */
    fun isValid(): Boolean

    /**
     * Returns the bounty objectives, as a [List<IPickedEntry>]
     */
    fun objectiveContent(): List<IPickedEntry>

    /**
     * Returns the rewards, as a [List<IPickedEntry>]
     */
    fun rewardContent(): List<IPickedEntry>
}