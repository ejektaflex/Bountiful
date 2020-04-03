[B114](../../index.md) / [ejektaflex.bountiful.gui.slot](../index.md) / [BountySlot](./index.md)

# BountySlot

`class BountySlot : SlotItemHandler` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/gui/slot/BountySlot.kt#L13)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BountySlot(board: `[`BoardTileEntity`](../../ejektaflex.bountiful.block/-board-tile-entity/index.md)`, index: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, x: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, y: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [board](board.md) | `val board: `[`BoardTileEntity`](../../ejektaflex.bountiful.block/-board-tile-entity/index.md) |

### Functions

| Name | Summary |
|---|---|
| [isItemValid](is-item-valid.md) | `fun isItemValid(stack: ItemStack): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onSlotChanged](on-slot-changed.md) | `fun onSlotChanged(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onTake](on-take.md) | `fun onTake(thePlayer: PlayerEntity, stack: ItemStack): ItemStack` |
