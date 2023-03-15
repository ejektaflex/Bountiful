package ejektaflex.bountiful.ext

import ejektaflex.bountiful.util.IWeighted
import ejektaflex.bountiful.util.ItemRange
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSource
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

val String.rl: ResourceLocation
    get() = ResourceLocation(substringBefore(":"), substringAfter(":"))

fun CommandSource.sendMessage(str: String) {
    sendSystemMessage(Component.literal(str))
}

fun CommandSource.sendTranslation(str: String) {
    sendSystemMessage(Component.translatable(str))
}

fun CommandSource.sendErrorMsg(str: String) {
    this.sendSystemMessage(Component.literal(str).withStyle(ChatFormatting.RED))
}

// TODO Make update this
fun Entity.sendTranslation(key: String) = sendSystemMessage(Component.translatable(key))

fun Component.withSibling(component: Component): Component {
    siblings.add(component)
    return this
}

val IntRange.ir: ItemRange
    get() = ItemRange(this.first, this.last)

// Some ASM transformers don't like Kotlin's random class :(

private val hackyRandMaker = java.util.Random()

fun IntRange.hackyRandom(): Int {

    return start + hackyRandMaker.nextInt(endInclusive - start + 1)
}

inline fun <reified T : Any> supposedlyNotNull(list: List<T>): NonNullList<T> {
    return NonNullList.of(list.first(), *list.toTypedArray())
}

fun <T : Any> List<T>.hackyRandom(): T {
    return this[(indices).hackyRandom()]
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
    val sum = this.sumOf { it.normalizedWeight(exp) }
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
