package ejektaflex.bountiful.data.entry

import kotlin.reflect.KClass

enum class BountyType(val id: String, val klazz: KClass<out BountyEntry>) {
    Item("item", BountyEntryItem::class),
    ItemTag("item-tag", BountyEntryItemTag::class),
    Entity("entity", BountyEntryEntity::class)
}