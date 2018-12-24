package bountiful.ext

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.client.Minecraft
import net.minecraft.command.ICommandSender
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentString
import java.io.InputStream

val String.rl: ResourceLocation
    get() = ResourceLocation(substringBefore(":"), substringAfter(":"))

val ResourceLocation.inputStream: InputStream?
    get() = Minecraft.getMinecraft().resourceManager.getResource(this).inputStream

val ResourceLocation.toStringContents: String?
    get() = inputStream?.bufferedReader()?.readText()

fun ICommandSender.sendMessage(str: String) = sendMessage(TextComponentString(str))

