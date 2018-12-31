package ejektaflex.bountiful.api.logic.pickable

import com.google.gson.annotations.SerializedName
import net.minecraft.nbt.NBTTagCompound

open class PickedEntry(
        override var content: String = "UNDEFINED",
        @SerializedName("unitWorth")
        override var amount: Int = Integer.MIN_VALUE
) : IPickedEntry {

    override fun timeMult() = 1.0

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setString("content", content)
            setInteger("amount", amount)
        }
    }

    override fun deserializeNBT(tag: NBTTagCompound) {
        content = tag.getString("content")
        amount = tag.getInteger("amount")
    }


    override fun toString() = "Picked(§f${amount}x §6$content)"

    override val prettyContent: String
        get() = toString()

    override fun typed(): IPickedEntry {
        if (":" in content) {
            return when (val type = content.substringBefore(':')) {
                "entity" -> PickedEntryEntity(this)
                else -> PickedEntryStack(this)
            }
        } else {
            throw Exception("Entry: '$content' is has no comma prefix!")
        }
    }

    override val contentObj: Any? = null


}