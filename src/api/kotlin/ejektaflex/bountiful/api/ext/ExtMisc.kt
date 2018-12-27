package ejektaflex.bountiful.api.ext

import net.minecraft.client.Minecraft
import net.minecraft.command.ICommandSender
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentString
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min

val String.rl: ResourceLocation
    get() = ResourceLocation(substringBefore(":"), substringAfter(":"))

val ResourceLocation.inputStream: InputStream?
    get() = Minecraft.getMinecraft().resourceManager.getResource(this).inputStream

val ResourceLocation.toStringContents: String?
    get() = inputStream?.bufferedReader()?.readText()

fun ICommandSender.sendMessage(str: String) = sendMessage(TextComponentString(str))

fun Int.clampTo(range: IntRange): Int {
    return max(range.first, min(this, range.last))
}

fun Double.clampTo(low: Double, high: Double): Double {
    return max(low, min(this, high))
}

fun Long.clampTo(range: LongRange): Long {
    return max(range.first, min(this, range.last))
}