package io.ejekta.kambrik.ext

import net.minecraft.block.DispenserBlock
import net.minecraft.block.dispenser.ProjectileDispenserBehavior
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Util
import net.minecraft.util.math.Position
import net.minecraft.world.World



fun Item.addSimpleDispenserBehavior(outEntity: (world: World?, position: Position, stack: ItemStack?) -> ProjectileEntity) {
    DispenserBlock.registerBehavior(this, object : ProjectileDispenserBehavior() {
        override fun createProjectile(world: World?, position: Position, stack: ItemStack?): ProjectileEntity {
            return outEntity(world, position, stack)
        }
    })
}

