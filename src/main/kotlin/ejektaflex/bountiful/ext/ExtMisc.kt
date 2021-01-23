package ejektaflex.bountiful.ext

import ejektaflex.bountiful.util.IWeighted
import ejektaflex.bountiful.util.ItemRange
import net.minecraft.command.CommandSource
import net.minecraft.entity.Entity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.*
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

fun CommandSource.sendMessage(str: String) {
    sendFeedback(StringTextComponent(str), true)
}

fun CommandSource.sendTranslation(str: String) {
    sendFeedback(TranslationTextComponent(str), true)
}

fun CommandSource.sendErrorMsg(str: String) {
    sendErrorMessage(StringTextComponent(str))
}

// TODO Make update this
fun Entity.sendTranslation(key: String) = sendMessage(StringTextComponent("Key: $key"), uniqueID)

fun ServerPlayerEntity.sendTranslation() {

}

fun IFormattableTextComponent.modStyle(func: Style.() -> Unit): IFormattableTextComponent {
    style = style.apply(func)
    return this
}

fun ITextComponent.colored(color: TextFormatting): ITextComponent {
    style.color = Color.fromTextFormatting(color)
    return this
}

fun ITextComponent.withSibling(component: ITextComponent): ITextComponent {
    siblings.add(component)
    return this
}

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

// Some ASM transformers don't like Kotlin's random class :(

private val hackyRandMaker = java.util.Random()

fun IntRange.hackyRandom(): Int {

    return start + hackyRandMaker.nextInt(endInclusive - start + 1)
}

inline fun <reified T : Any> supposedlyNotNull(list: List<T>): NonNullList<T> {
    return NonNullList.from<T>(list.first(), *list.toTypedArray())
}

fun <T : Any> List<T>.hackyRandom(): T {
    return this[(0 until size).hackyRandom()]
}

val <T : IWeighted> List<T>.weightedRandom: T
    get() {
        //if (size == 1) return first()
        val sum = this.sumBy { it.weight }
        var point = (0..sum).hackyRandom()
        for (item in this) {
            if (point <= item.weight) {
                return item
            }
            point -= item.weight
        }
        return last()
    }


fun <T : IWeighted> List<T>.weightedRandomNorm(exp: Double): T {
    //if (size == 1) return first()
    val sum = this.sumBy { it.normalizedWeight(exp) }
    var point = (0..sum).hackyRandom()
    for (item in this) {
        if (point <= item.normalizedWeight(exp)) {
            return item
        }
        point -= item.normalizedWeight(exp)
    }
    return last()
}

fun randomSplit(num: Int, ways: Int): List<Int> {
    val bits = (0 until ways).map { hackyRandMaker.nextDouble() }
    val sum = bits.sum()
    return bits.map { (it / sum) * num }.map { it.toInt() }
}
