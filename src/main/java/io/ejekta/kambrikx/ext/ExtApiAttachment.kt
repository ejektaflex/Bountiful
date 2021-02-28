package io.ejekta.kambrikx.ext

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrikx.api.nbt.KambrikNbtApi

private val NbtApiAttachment = KambrikNbtApi()

val Kambrik.NBT: KambrikNbtApi
    get() = NbtApiAttachment