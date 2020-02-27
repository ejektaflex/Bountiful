package ejektaflex.bountiful.api.data.entry

import kotlin.reflect.KClass

enum class BountyType(val id: String, val klazz: KClass<out BountyEntry>) {
    Stack("stack", BountyEntryStack::class),
    ItemTag("tag", BountyEntryItemTag::class),
    Entity("entity", BountyEntryEntity::class)
}