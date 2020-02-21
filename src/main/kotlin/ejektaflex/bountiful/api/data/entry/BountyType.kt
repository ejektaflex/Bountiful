package ejektaflex.bountiful.api.data.entry

import kotlin.reflect.KClass

enum class BountyType(val id: String, val klazz: KClass<out BountyEntry>) {
    Stack("stack", BountyEntryStack::class),
    Entity("entity", BountyEntryEntity::class)
}