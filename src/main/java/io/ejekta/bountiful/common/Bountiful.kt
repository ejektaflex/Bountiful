@file:UseSerializers(IdentitySer::class)
package io.ejekta.bountiful.common

import io.ejekta.bountiful.common.serial.IdentitySer
import io.ejekta.bountiful.common.serial.Test
import io.ejekta.bountiful.common.util.JsonStrict.toJson
import io.ejekta.bountiful.common.util.JsonStrict.toTag
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import net.fabricmc.api.ModInitializer
import net.minecraft.client.realms.util.JsonUtils
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.LongArrayTag
import net.minecraft.nbt.NbtHelper
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import kotlin.reflect.typeOf

class Bountiful : ModInitializer {
    val ID = "bountiful"

    @ExperimentalStdlibApi
    inline fun <reified T : Any> doot() {
        println("DOOT: ${typeOf<T>()}")
    }

    @ExperimentalSerializationApi
    @ExperimentalStdlibApi
    override fun onInitialize() {
        println("Common init")

        val nbt = CompoundTag().apply {
            putString("hello", "there")
        }

        val jsonish = buildJsonObject {
            put("hi", "sir")
            putJsonArray("alist") {
                add("of")
                add("stuff")
                add(4)
            }
        }

        val tag = LongArrayTag(listOf(1L, 2L, 3L))

        println("NBT: $nbt")

        println("JSON: $jsonish")

        val tagged = jsonish.toTag()

        println("TAGGED: $tagged")

        val jsonAgain = tagged.toJson()

        println("JSONAGAIN: $jsonAgain")

        println("TAGFROMLONG: ${tag.toJson().toTag().toJson().toTag()::class.simpleName}")

        /*
        println(realJson.encodeToString(
            Test(Identifier("bountiful", "blahblah"))
        ))

         */

        //Json.par

        //Json.stringify()


        throw Exception("no")



    }
}