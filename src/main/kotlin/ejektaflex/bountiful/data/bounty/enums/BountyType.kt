package ejektaflex.bountiful.data.bounty.enums

import ejektaflex.bountiful.data.bounty.*
import kotlin.reflect.KClass

enum class BountyType(val id: String, val klazz: KClass<out BountyEntry>) {
    Item("item", BountyEntryItem::class),
    ItemTag("item-tag", BountyEntryItemTag::class),
    Entity("entity", BountyEntryEntity::class),
    Command("command", BountyEntryCommand::class),
    Experience("experience", BountyEntryExperience::class)
}