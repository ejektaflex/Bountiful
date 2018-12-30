package ejektaflex.bountiful.api.logic.pickable

import com.google.gson.annotations.SerializedName
import net.minecraft.nbt.NBTTagCompound

open class PickedEntry(
        override var contentID: String = "UNDEFINED",
        @SerializedName("unitWorth")
        override var amount: Int = Integer.MIN_VALUE
) : IPickedEntry {

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setString("contentID", contentID)
            setInteger("amount", amount)
        }
    }

    override fun deserializeNBT(tag: NBTTagCompound) {
        contentID = tag.getString("contentID")
        amount = tag.getInteger("amount")
    }


    override fun toString() = "Picked(§f${amount}x §6$contentID)"

    override val prettyContent: String
        get() = toString()

    override fun typed(): IPickedEntry {
        if (":" in contentID) {
            return when (val type = contentID.substringBefore(':')) {
                "doot" -> PickedEntryStack(this)
                //"entity" ->
                else -> PickedEntryStack(this)
            }
        } else {
            throw Exception("Entry: '$contentID' is has no comma prefix!")
        }
    }

    override val content: Any? = null


}