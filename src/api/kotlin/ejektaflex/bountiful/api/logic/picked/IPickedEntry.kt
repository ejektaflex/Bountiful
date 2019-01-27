package ejektaflex.bountiful.api.logic.picked

import ejektaflex.bountiful.api.data.IValidatable
import ejektaflex.bountiful.api.data.IWeighted
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

interface IPickedEntry : INBTSerializable<NBTTagCompound>, IValidatable, Cloneable, IWeighted {
    /**
     * A string representing the content of this picked entry
     */
    var content: String

    /**
     * An integer representing the amount of this content you need to complete the bounty
     */
    var amount: Int

    /**
     * The relative chances of this entry being picked compared to a different one
     */
    override var weight: Int

    /**
     * The accompanying NBT data of the picked item, if there is any
     */
    val tag: NBTTagCompound?

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