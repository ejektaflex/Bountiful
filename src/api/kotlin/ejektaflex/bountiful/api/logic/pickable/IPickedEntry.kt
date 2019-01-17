package ejektaflex.bountiful.api.logic.pickable

import ejektaflex.bountiful.api.data.IValidatable
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

interface IPickedEntry : INBTSerializable<NBTTagCompound>, IValidatable {
    /**
     * A string representing the content of this picked entry
     */
    var content: String

    /**
     * An integer representing the amount of this content you need to complete the bounty
     */
    var amount: Int

    /**
     * The subtype ([PickedEntryStack], [PickedEntryEntity], etc) of this entry
     */
    fun typed(): IPickedEntry

    /**
     * The object version of this content, accessible regardless of subtype
     */
    val contentObj: Any?

    /**
     * The content of this entry formatted as a string. Meant for display on tooltips.
     */
    val prettyContent: String

    /**
     * A multiplier for how long this item gets to complete.
     */
    fun timeMult(): Double
}