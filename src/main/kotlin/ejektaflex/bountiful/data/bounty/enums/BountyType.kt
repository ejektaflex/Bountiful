package ejektaflex.bountiful.data.bounty.enums

import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.data.bounty.BountyEntryEntity
import ejektaflex.bountiful.data.bounty.BountyEntryItem
import ejektaflex.bountiful.data.bounty.BountyEntryItemTag
import kotlin.reflect.KClass

enum class BountyType(val id: String, val klazz: KClass<out BountyEntry>) {
    Item("item", BountyEntryItem::class),
    ItemTag("item-tag", BountyEntryItemTag::class),
    Entity("entity", BountyEntryEntity::class)
}