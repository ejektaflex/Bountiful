package io.ejekta.bountiful.client

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.messages.ClipboardCopy
import io.ejekta.bountiful.mixin.ModelPredicateProviderRegistrar
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.gui.screens.DecreeAnalyzerScreen
import io.ejekta.kambrik.gui.screens.RegistryPickerScreen
import kotlinx.serialization.InternalSerializationApi
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.lwjgl.glfw.GLFW

class BountifulClient : ClientModInitializer {

    @InternalSerializationApi
    override fun onInitializeClient() {
        println("Init client for Bountiful")

        ModelPredicateProviderRegistrar.registerInvoker(
            BountifulContent.BOUNTY_ITEM,
            Bountiful.id("rarity")
        ) { itemStack: ItemStack, _: ClientWorld?, _: LivingEntity?, _: Int ->
            val rarity = BountyData[itemStack].rarity
            rarity.ordinal.toFloat() / 10f
        }

        ModelPredicateProviderRegistrar.registerInvoker(
            BountifulContent.DECREE_ITEM,
            Bountiful.id("status")
        ) { itemStack: ItemStack, _: ClientWorld?, _: LivingEntity?, _: Int ->
            val data = DecreeData[itemStack]
            if (data.ids.isNotEmpty()) 1f else 0f
        }

        Kambrik.Message.registerClientMessage(
            ClipboardCopy.serializer(),
            Bountiful.id("clipboard_copy")
        )

        ScreenRegistry.register(BountifulContent.BOARD_SCREEN_HANDLER, ::BoardScreen)

        Kambrik.Input.registerKeyboardBinding(GLFW.GLFW_KEY_U, "Registry Picker", "Kambrik Misc") {
            onDown {
                println("Opening screen!")
                MinecraftClient.getInstance().setScreen(
                    RegistryPickerScreen(Registry.BLOCK_ENTITY_TYPE)
                )
            }
        }

        Kambrik.Input.registerKeyboardBinding(GLFW.GLFW_KEY_Y, "Decree Analyzer", "Kambrik Misc") {
            onDown {
                println("Opening screen!")
                MinecraftClient.getInstance().setScreen(
                    DecreeAnalyzerScreen(BountifulContent.Decrees.random())
                )
            }
        }

    }

}