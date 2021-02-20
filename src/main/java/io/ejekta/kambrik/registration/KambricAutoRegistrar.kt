package io.ejekta.kambrik.registration

import io.ejekta.bountiful.common.content.board.BoardBlockEntity
import io.ejekta.kambrik.internal.KambrikMarker
import io.ejekta.kambrik.internal.KambrikRegistrar
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.Item
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.Supplier


interface KambricAutoRegistrar : KambrikMarker {

    fun manualRegister()

    fun <T> String.forRegistration(reg: Registry<T>, obj: T): T {
        return KambrikRegistrar.register(this@KambricAutoRegistrar, reg, this, obj)
    }

    infix fun String.forItem(item: Item): Item = forRegistration(Registry.ITEM, item)

    infix fun String.forBlock(block: Block): Block = forRegistration(Registry.BLOCK, block)

    fun <T : BlockEntity>String.forBlockEntity(block: Block, entity: () -> T): BlockEntityType<T>? {
        return BlockEntityType.Builder.create(Supplier(entity), block).build(null).also {
            forRegistration(Registry.BLOCK_ENTITY_TYPE, it)
        }
    }

    fun <T : ScreenHandler> forExtendedScreen(id: Identifier, factory: ScreenHandlerRegistry.ExtendedClientHandlerFactory<T>): ScreenHandlerType<T>? {
        return ScreenHandlerRegistry.registerExtended(id, factory)
    }



}