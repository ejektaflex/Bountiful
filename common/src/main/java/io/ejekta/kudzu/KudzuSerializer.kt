package io.ejekta.kudzu

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object KudzuSerializer : KSerializer<KudzuVine> {

    object JsonObjectDescriptor : SerialDescriptor by serialDescriptor<HashMap<String, JsonElement>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.json.JsonObject"
    }

    override val descriptor: SerialDescriptor = JsonObjectDescriptor

    override fun serialize(encoder: Encoder, value: KudzuVine) {
        encoder.encodeSerializableValue(JsonObject.serializer(), value.toJsonObject())
    }

    override fun deserialize(decoder: Decoder): KudzuVine {
        return decoder.decodeSerializableValue(JsonObject.serializer()).toKudzu()
    }

}