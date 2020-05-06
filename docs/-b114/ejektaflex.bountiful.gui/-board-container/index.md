[B114](../../index.md) / [ejektaflex.bountiful.gui](../index.md) / [BoardContainer](./index.md)

# BoardContainer

`class BoardContainer : Container` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/gui/BoardContainer.kt#L18)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BoardContainer(id: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, world: World, pos: BlockPos, inv: PlayerInventory)` |

### Properties

| Name | Summary |
|---|---|
| [inv](inv.md) | `val inv: PlayerInventory` |
| [pos](pos.md) | `val pos: BlockPos` |
| [world](world.md) | `val world: World` |

### Functions

| Name | Summary |
|---|---|
| [canInteractWith](can-interact-with.md) | `fun canInteractWith(playerIn: PlayerEntity): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [transferStackInSlot](transfer-stack-in-slot.md) | `fun transferStackInSlot(player: PlayerEntity, index: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): ItemStack` |
