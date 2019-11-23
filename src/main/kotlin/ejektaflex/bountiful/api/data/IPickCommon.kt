package ejektaflex.bountiful.api.data

import ejektaflex.bountiful.api.generic.IStageRequirement

interface IPickCommon : IStageRequirement {
    /**
     * A string representing the content of this picked entry
     */
    var content: String

    /**
     * Any GameStages stages that this entry might have
     */
    var stages: MutableList<String>?

    /**
     * A method of safely getting any stages required for this object
     */
    override fun requiredStages() = stages ?: mutableListOf()
}