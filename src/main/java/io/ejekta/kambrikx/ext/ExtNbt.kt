package io.ejekta.kambrikx.ext

import io.ejekta.kambrikx.api.nbt.NbtMode
import io.ejekta.kambrikx.api.nbt.TagConverterLenient
import io.ejekta.kambrikx.api.nbt.TagConverterStrict
import kotlinx.serialization.json.JsonElement
import net.minecraft.nbt.Tag

private const val TAG_TYPE = "_tagtype"
private const val TAG_DATA = "_tagdata"

fun Tag.toLenientJson() = TagConverterLenient.toJson(this)
fun Tag.toStrictJson() = TagConverterStrict.toJson(this)
fun Tag.toJson(mode: NbtMode = NbtMode.LENIENT) = mode.converter.toJson(this)

fun JsonElement.toStrictTag() = TagConverterStrict.toTag(this)
fun JsonElement.toLenientTag() = TagConverterLenient.toTag(this)
fun JsonElement.toTag(mode: NbtMode = NbtMode.LENIENT) = mode.converter.toTag(this)