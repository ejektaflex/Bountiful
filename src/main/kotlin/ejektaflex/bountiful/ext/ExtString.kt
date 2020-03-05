package ejektaflex.bountiful.ext

import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries

val String.toEntityType: EntityType<*>?
    get() {
        return ForgeRegistries.ENTITIES.entries.find {
            it.key.toString() == this
        }?.value
    }


val String.toItemStack: ItemStack?
    get() {
        val sect = split(":").toMutableList()
        if (sect.size !in 2..3) {
            return null
        } else if (sect.size == 2) {
            sect += "0"
        }
        val item = ForgeRegistries.ITEMS.getValue(ResourceLocation("${sect[0]}:${sect[1]}"))
        //val item = Item.getByNameOrId("${sect[0]}:${sect[1]}")
        return if (item != null) {
            ItemStack(item, 1)
        } else {
            null
        }
    }

val ItemStack.toPretty: String
    get() {
        var proto = item.registryName.toString()

        return proto
    }

/**
 * Oak Log -> "minecraft:log:0"
 */
/*
val IBlockState.pretty: String
    get() {
        return block.registryName.toString()
    }


 */
val String.toItem: Item?
    get() = toItemStack?.item