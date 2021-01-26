package ejektaflex.bountiful.data.json

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.data.bounty.enums.BountyType

/**
 * Contains all of our data serializers and deserializers
 */
object JsonSerializers {

    fun register() {
        println("BOREG registering json ser")
        JsonAdapter.register(bountyDeserializer)
        JsonAdapter.register(bountySerializer)
    }

    private val bountyDeserializer: JsonDeserializer<BountyEntry> = JsonDeserializer { json, typeOfT, context ->
        val jsonType = json.asJsonObject.get("type").asString
        val jsonName = json.asJsonObject.get("content").asString

        val bType = BountyType.values().find { jsonType == it.id }
                ?: throw Exception("Incorrect type for bounty: $jsonType. Content was: $jsonName")

        JsonAdapter.fromJson(json, bType.klazz)
    }

    private val bountySerializer: JsonSerializer<BountyEntry> = JsonSerializer { src, typeOfSrc, context ->
        JsonAdapter.toJsonTree(src, src::class)
    }

}