package ejektaflex.bountiful.api.data.entry

import kotlin.reflect.KClass

enum class BountyType(val ids: List<String>, val klazz: KClass<out BountyEntry>) {
    Stack(listOf("stack", "tag"), BountyEntryStack::class),
    Entity(listOf("entity"), BountyEntryEntity::class)
}