[B114](../../index.md) / [ejektaflex.bountiful.block](../index.md) / [BoardTileEntity](./index.md)

# BoardTileEntity

`class BoardTileEntity : TileEntity, ITickableTileEntity, INamedContainerProvider` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/block/BoardTileEntity.kt#L27)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BoardTileEntity()` |

### Properties

| Name | Summary |
|---|---|
| [handler](handler.md) | `val handler: ItemStackHandler` |
| [newBoard](new-board.md) | `var newBoard: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [numDecrees](num-decrees.md) | `val numDecrees: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [pulseLeft](pulse-left.md) | `var pulseLeft: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [createMenu](create-menu.md) | `fun createMenu(i: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, inv: PlayerInventory, player: PlayerEntity): Container?` |
| [getCapability](get-capability.md) | `fun <T> getCapability(cap: Capability<T>): LazyOptional<T>` |
| [getDisplayName](get-display-name.md) | `fun getDisplayName(): ITextComponent` |
| [read](read.md) | `fun read(nbt: CompoundNBT): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [tick](tick.md) | `fun tick(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [write](write.md) | `fun write(compound: CompoundNBT): CompoundNBT` |

### Companion Object Properties

| Name | Summary |
|---|---|
| [SIZE](-s-i-z-e.md) | `const val SIZE: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
