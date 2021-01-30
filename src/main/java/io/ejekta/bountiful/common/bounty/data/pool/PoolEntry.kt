package io.ejekta.bountiful.common.bounty.data.pool

import io.ejekta.bountiful.common.bounty.logic.BountyDataEntry
import io.ejekta.bountiful.common.bounty.logic.BountyType
import io.ejekta.bountiful.common.serial.Format
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.Tag

@Serializable
class PoolEntry private constructor() {
    var type = BountyType.NULL
    var content = "Nope"
    var amount = EntryRange(-1, -1)
    var unitWorth = -1000.0
    var weight = -1000.0
    var timeMult = 0.0

    var mystery: Boolean = false

    var nbt: String? = null


    var nbtData: Tag?
        get() = nbt?.let { StringNbtReader.parse(it) }
        set(value) {
            nbt = value?.asString()
        }

    fun save() = Format.DataPack.encodeToString(serializer(), this)

    fun toEntry(): BountyDataEntry {
        return BountyDataEntry(type, content, amount.pick(), nbt)
    }

    @Serializable
    class EntryRange(val min: Int, val max: Int) {
        fun pick(): Int = (min..max).random()
    }

    companion object {

        // With encodeDefaults = false, we need a separate constructor
        fun create() = PoolEntry().apply {
            type = BountyType.ITEM
            amount = EntryRange(1, 1)
            content = "NO_CONTENT"
            unitWorth = 1000.0
            weight = 1000.0
            timeMult = 1.0
        }

    }

}