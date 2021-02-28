package io.ejekta.kambrikx.api.nbt

enum class NbtMode(val converter: TagConverter) {
    LENIENT(TagConverterLenient),
    STRICT(TagConverterStrict)
}