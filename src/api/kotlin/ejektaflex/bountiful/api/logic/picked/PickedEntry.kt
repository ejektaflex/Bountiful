package ejektaflex.bountiful.api.logic.picked

import com.google.gson.annotations.SerializedName
import net.minecraft.nbt.*

open class PickedEntry(
        override var content: String = "UNDEFINED",
        @SerializedName("unitWorth")
        override var amount: Int = Integer.MIN_VALUE,
        @SerializedName("nbt_data")
        var nbtJson: Any? = null
) : IPickedEntry {

    override val nbt: NBTTagCompound?
        get() {
            return when (nbtJson) {
                null -> null
                is String -> JsonToNBT.getTagFromJson(nbtJson.toString())
                is NBTTagCompound -> nbtJson as NBTTagCompound
                else -> throw Exception("NBT $nbtJson must be a String! Instead was a: ${nbtJson!!::class}")
            }
        }

    override fun timeMult() = 1.0

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setString("content", content)
            setInteger("amount", amount)
            nbt?.let {
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
        val isNBTValid = (nbtJson == null) || (nbt != null)
        return contentObj != null && isNBTValid
    }


}