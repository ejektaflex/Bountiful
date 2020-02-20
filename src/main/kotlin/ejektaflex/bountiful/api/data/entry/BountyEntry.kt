package ejektaflex.bountiful.api.data.entry


import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.api.data.ITagString
import ejektaflex.bountiful.api.data.JsonBiSerializer
import ejektaflex.bountiful.api.data.entry.feature.IEntryFeature
import ejektaflex.bountiful.api.ext.hackyRandom
import ejektaflex.bountiful.api.generic.IWeighted
import ejektaflex.bountiful.api.generic.ItemRange
import net.minecraft.nbt.*
import net.minecraftforge.common.util.INBTSerializable

abstract class BountyEntry<T : IEntryFeature> : ITagString, JsonBiSerializer<BountyEntry<T>>, INBTSerializable<CompoundNBT>, IWeighted, Cloneable {

    abstract var type: String

    open var content: String = ""

    @SerializedName("nbt_data")
    override var nbtString: String? = null

    open var amountRange: ItemRange? = null

    open var unitWorth: Int = Integer.MIN_VALUE

    //@Expose(serialize = false)
    override var weight: Int = 100

    abstract val calculatedWorth: Int

    fun cloned(): BountyEntry<T> {
        return clone() as BountyEntry<T>
    }

    abstract fun pick(): BountyEntry<T>

    val randCount: Int
        get() = ((amountRange?.min ?: 1)..(amountRange?.max ?: Int.MAX_VALUE)).hackyRandom()

    @Transient open val feature: T? = null


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
            feature?.serializeNBT(this)
        }
    }

    override fun deserializeNBT(tag: CompoundNBT) {
        type = tag.getString("type")
        content = tag.getString("content")
        unitWorth = tag.getInt("unitWorth")
        if ("nbt" in tag) {
            nbtString = tag["nbt"]!!.toString()
        }
        feature?.deserializeNBT(tag)
    }

    open val prettyContent: String
        get() = toString()


    val minValueOfPick: Int
        get() = unitWorth * (amountRange?.min ?: 1)

}