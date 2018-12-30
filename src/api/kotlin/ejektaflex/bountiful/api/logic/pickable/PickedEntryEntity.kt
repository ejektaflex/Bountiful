package ejektaflex.bountiful.api.logic.pickable

import com.google.gson.annotations.Expose
import net.minecraft.entity.EntityLiving
import net.minecraftforge.fml.common.registry.EntityRegistry
import net.minecraftforge.fml.common.registry.ForgeRegistries

class PickedEntryEntity(
        @Expose(serialize = false, deserialize = false)
        val genericPick: PickedEntry
) : IPickedEntry by genericPick {


    val entityLiving: EntityLiving?
        get() {
            return null
        }

    override val content: Any?
        get() = entityLiving

    override val prettyContent: String
        get() = entityLiving?.name ?: "Null Living Entity, No Name"

    override fun toString(): String {
        return "§r" + amount.toString() + "x §a" + (entityLiving?.displayName ?: "Unknown Item (Content ID: $contentID)") + "§r"
    }

}