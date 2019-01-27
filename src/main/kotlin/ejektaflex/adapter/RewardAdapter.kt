package ejektaflex.adapter

import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.api.logic.picked.PickedEntry
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack

data class RewardAdapter(
        var content: String,
        var amount: Int,
        var weight: Int,
        @SerializedName("nbt_data")
        var nbtData: String? = null
) : IAdapter<PickedEntryStack> {
    override fun adapt(): PickedEntryStack {
        return PickedEntryStack(PickedEntry(content, amount, weight, nbtData))
    }
}