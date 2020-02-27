package ejektaflex.bountiful.api.data.entry


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.api.data.ITagString
import ejektaflex.bountiful.api.data.JsonBiSerializer
import ejektaflex.bountiful.api.data.entry.feature.IEntryFeature
import ejektaflex.bountiful.api.ext.hackyRandom
import ejektaflex.bountiful.api.generic.IWeighted
import ejektaflex.bountiful.api.generic.ItemRange
import ejektaflex.bountiful.logic.BountyProgress
import net.minecraft.nbt.*
import net.minecraftforge.common.util.INBTSerializable
import kotlin.math.abs
import kotlin.math.min

abstract class BountyEntry : ITagString, JsonBiSerializer<BountyEntry>, INBTSerializable<CompoundNBT>, IWeighted, Cloneable {

    class EntryValidationException(reason: String) : Exception("An entry has failed validation and will not be loaded. Reason: $reason. Skipping entry..")

    open var type: String = "UNKNOWN_TYPE"

    @Expose
    open var name: String? = null

    @Expose
    open var content: String = ""

    @SerializedName("nbt_data")
    override var nbtString: String? = null

    @Expose
    open var amountRange: ItemRange = ItemRange(1, 1)

    @Expose
    open var unitWorth: Int = Integer.MIN_VALUE

    @Expose
    override var weight: Int = 100

    abstract val calculatedWorth: Int

    var amount = 0

    private val worthRange: IntRange
        get() = (amountRange!!.min * unitWorth)..(amountRange!!.max * unitWorth)

    fun worthDistanceFrom(value: Int): Int {
        val rnge = worthRange
        return if (value in rnge) {
            0
        } else {
            min(abs(rnge.first - value), abs(rnge.last - value))
        }
    }

    fun cloned(): BountyEntry {
        return clone() as BountyEntry
    }

    abstract fun validate()

    abstract fun pick(worth: Int? = null): BountyEntry

    val randCount: Int
        get() = ((amountRange?.min ?: 1)..(amountRange?.max ?: Int.MAX_VALUE)).hackyRandom()


    // Must override because overriding [nbtString]
    override val tag: CompoundNBT?
        get() = super.tag


    override fun serializeNBT(): CompoundNBT {
        return CompoundNBT().apply {
            putString("type", type)
            putString("content", content)
            putInt("unitWorth", unitWorth)
            tag?.let {
                this.put("nbt", it)
            }
            putInt("amount", amount)
            name?.let {
                this.putString("name", it)
            }
        }
    }

    override fun deserializeNBT(tag: CompoundNBT) {
        type = tag.getString("type")
        content = tag.getString("content")
        unitWorth = tag.getInt("unitWorth")
        if ("nbt" in tag) {
            nbtString = tag["nbt"]!!.toString()
        }
        amount = tag.getInt("amount")
        if ("name" in tag) {
            name = tag.getString("name")
        }
    }

    abstract val formattedName: String

}