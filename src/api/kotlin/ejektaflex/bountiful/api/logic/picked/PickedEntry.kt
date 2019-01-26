package ejektaflex.bountiful.api.logic.picked

import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.api.data.IHasTag
import net.minecraft.nbt.*

open class PickedEntry(
        override var content: String = "UNDEFINED",
        @SerializedName("unitWorth")
        override var amount: Int = Integer.MIN_VALUE,
        @SerializedName("nbt_data")
        override var nbtJson: String? = null
) : IPickedEntry, IHasTag {

    // Must override because overriding [nbtJson]
    override val tag: NBTTagCompound?
        get() = super.tag

    override fun timeMult() = 1.0

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setString("content", content)
            setInteger("amount", amount)
            tag?.let {
                setTag("nbt", it)
            }
        }
    }

    override fun deserializeNBT(tag: NBTTagCompound) {
        content = tag.getString("content")
        amount = tag.getInteger("amount")
        if (tag.hasKey("nbt")) {
            nbtJson = tag.getCompoundTag("nbt").toString()
        }
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
            throw Exception("Entry: '$content' is has no colon prefix!")
        }
    }

    override val contentObj: Any? = null

    override fun isValid(): Boolean {
        val isNBTValid = (nbtJson == null) || (tag != null)
        return contentObj != null && isNBTValid
    }


}