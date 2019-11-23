package ejektaflex.bountiful.api.ext

import ejektaflex.bountiful.api.generic.IWeighted
import ejektaflex.bountiful.api.generic.ItemRange
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.StringTextComponent
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.registries.ForgeRegistries
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min

/*
fun <T : INBTSerializable<CompoundNBT>> World.ifHasCapability(capability: Capability<T>, func: T.() -> Unit) {
    if (hasCapability(capability, null)) {
        func(getCapability(capability, null)!!)
    }
}
*/

val String.rl: ResourceLocation
    get() = ResourceLocation(substringBefore(":"), substringAfter(":"))

val ResourceLocation.inputStream: InputStream?
    get() = Minecraft.getInstance().resourceManager.getResource(this).inputStream

val ResourceLocation.toStringContents: String?
    get() = inputStream?.bufferedReader()?.readText()


/*
val Entity.registryName: ResourceLocation?
    get() {
        val valid = ForgeRegistries.ENTITIES.entries.filter {
            this::class.java.isAssignableFrom(it.value.entityClass) &&
                    it.value.entityClass.isAssignableFrom(this::class.java)
        }
        return valid.firstOrNull()?.key
    }


 */

//fun ICommandSender.sendMessage(str: String) = sendMessage(StringTextComponent(str))

//fun ICommandSender.sendTranslation(key: String) = sendMessage(TextComponentTranslation(key))

fun Int.clampTo(range: IntRange): Int {
    return max(range.first, min(this, range.last))
}

fun Double.clampTo(low: Double, high: Double): Double {
    return max(low, min(this, high))
}

fun Long.clampTo(range: LongRange): Long {
    return max(range.first, min(this, range.last))
}

val IntRange.ir: ItemRange
    get() = ItemRange(this.first, this.last)

val <T : IWeighted> List<T>.weightedRandom: T
    get() {
        val sum = this.sumBy { it.weight }
        var point = (0..sum).random()
        for (item in this) {
            if (point <= item.weight) {
                return item
            }
            point -= item.weight
        }
        return last()
    }