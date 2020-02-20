package ejektaflex.bountiful.api.data.json

import com.google.gson.JsonDeserializer
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.api.data.entry.BountyEntryEntity
import ejektaflex.bountiful.api.data.entry.BountyEntryStack
import ejektaflex.bountiful.api.data.entry.BountyType

/**
 * Contains all of our data serializers and deserializers
 */
object JsonSerializers {

    fun register() {
        JsonAdapter.register(bountyDeserializer)
    }

    val bountyDeserializer: JsonDeserializer<BountyEntry<*>> = JsonDeserializer { json, typeOfT, context ->
        val jsonType = json.asJsonObject.get("type").asString
        val jsonName = json.asJsonObject.get("content").asString

        val bType = BountyType.values().find { it.id == jsonType }
                ?: throw Exception("Incorrect type for bounty: $jsonType. Content was: $jsonName")

        JsonAdapter.fromJson(json, bType.klazz)

    }
}