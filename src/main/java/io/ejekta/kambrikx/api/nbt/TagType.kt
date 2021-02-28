package io.ejekta.kambrikx.api.nbt

import net.minecraft.nbt.*
/*
enum class TagType(val shortname: String, val reader: TagReader<*>) {
    END_TAG("End", EndTag.READER),
    BYTE_TAG("Byte", ByteTag.READER),
    SHORT_TAG("Short", ShortTag.READER),
    INT_TAG("Int", IntTag.READER),
    LONG_TAG("Long", LongTag.READER),
    FLOAT_TAG("Float", FloatTag.READER),
    DOUBLE_TAG("Double", DoubleTag.READER),
    BYTE_ARRAY_TAG("ByteArray", ByteArrayTag.READER),
    STRING_TAG("String", StringTag.READER),
    LIST_TAG("List", ListTag.READER),
    COMPOUND_TAG("Compound", CompoundTag.READER),
    INT_ARRAY_TAG("IntArraY", IntArrayTag.READER),
    LONG_ARRAY_TAG("LongArrayTag", LongArrayTag.READER)
}

 */

enum class TagType(val shortname: String, val reader: TagReader<*>) {
    END_TAG("End", EndTag.READER),
    BYTE_TAG("Byte", ByteTag.READER),
    SHORT_TAG("Short", ShortTag.READER),
    INT_TAG("Int", IntTag.READER),
    LONG_TAG("Long", LongTag.READER),
    FLOAT_TAG("Float", FloatTag.READER),
    DOUBLE_TAG("Double", DoubleTag.READER),
    BYTE_ARRAY_TAG("ByteArray", ByteArrayTag.READER),
    STRING_TAG("String", StringTag.READER),
    LIST_TAG("List", ListTag.READER),
    COMPOUND_TAG("Compound", CompoundTag.READER),
    INT_ARRAY_TAG("IntArraY", IntArrayTag.READER),
    LONG_ARRAY_TAG("LongArrayTag", LongArrayTag.READER)
}