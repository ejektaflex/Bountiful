package ejektaflex.bountiful.data.entry


import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.data.json.JsonBiSerializer
import ejektaflex.bountiful.ext.hackyRandom
import ejektaflex.bountiful.generic.IWeighted
import ejektaflex.bountiful.generic.ItemRange
import net.minecraft.nbt.*
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.common.util.INBTSerializable
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

abstract class BountyEntry : JsonBiSerializer<BountyEntry>, INBTSerializable<CompoundNBT>, IWeighted, Cloneable {

    class EntryValidationException(reason: String) : Exception("An entry has failed validation and will not be loaded. Reason: $reason. Skipping entry..")

    open var type: String = "UNKNOWN_TYPE"

    @Expose
    open var name: String? = null

    @Expose
    open var content: String = ""

    @Expose
    @SerializedName("nbt")
    var jsonNBT: JsonElement? = null

    val nbtTag: CompoundNBT?
        get() {
            return when (jsonNBT) {
                null -> null
                is JsonElement -> JsonToNBT.getTagFromJson(jsonNBT.toString())
                else -> throw Exception("NBT $jsonNBT must be a String! Instead was a: ${jsonNBT!!::class}")
            }
        }



    @Expose
    open var amountRange: ItemRange = ItemRange(1, 1)

    val maxWorth: Int
        get() = unitWorth * amountRange.max

    val minWorth: Int
        get() = unitWorth * amountRange.min

    @Expose
    open var unitWorth: Int = Integer.MIN_VALUE

    @Expose
    open var timeMult: Double? = null

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



    open fun validate() {
        if (amountRange.min < 1) {
            throw EntryValidationException("'$content' cannot have an amount possibly less than 1!")
        }
        if (amountRange.min > amountRange.max) {
            throw EntryValidationException("'$content' cannot have a min amount greater than it's max!")
        }
    }

    open fun pick(worth: Int? = null): BountyEntry {
        return cloned().apply {
            amount = if (worth != null) {
                max(1, ceil(worth.toDouble() / unitWorth).toInt())
            } else {
                randCount
            }
            // Clamp amount into amount range
            amount = min(amount, amountRange.max)
            amount = max(amount, amountRange.min)
        }
    }

    val randCount: Int
        get() = ((amountRange?.min ?: 1)..(amountRange?.max ?: Int.MAX_VALUE)).hackyRandom()




    override fun serializeNBT(): CompoundNBT {
        return CompoundNBT().apply {
            putString("type", type)
            putString("content", content)
            putInt("unitWorth", unitWorth)
            nbtTag?.let {
                this.put("nbt", it)
            }
            putInt("amount", amount)
            name?.let {
                this.putString("name", it)
            }
        }
    }

    private val jsonParser = JsonParser()

    override fun deserializeNBT(tag: CompoundNBT) {
        type = tag.getString("type")
        content = tag.getString("content")
        unitWorth = tag.getInt("unitWorth")
        if ("nbt" in tag) {
            val comp = tag.get("nbt")
            val str = comp.toString()
            jsonNBT = jsonParser.parse(str)
            //jsonNBT = tag["nbt"]!!.toString()
        }
        amount = tag.getInt("amount")
        if ("name" in tag) {
            name = tag.getString("name")
        }
    }

    abstract val formattedName: ITextComponent

}