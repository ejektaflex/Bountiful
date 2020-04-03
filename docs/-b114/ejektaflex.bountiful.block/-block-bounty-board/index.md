[B114](../../index.md) / [ejektaflex.bountiful.block](../index.md) / [BlockBountyBoard](./index.md)

# BlockBountyBoard

`class BlockBountyBoard : Block` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/block/BlockBountyBoard.kt#L23)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BlockBountyBoard()` |

### Properties

| Name | Summary |
|---|---|
| [hardness](hardness.md) | `val hardness: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |

### Functions

| Name | Summary |
|---|---|
| [createTileEntity](create-tile-entity.md) | `fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity?` |
| [getBlockHardness](get-block-hardness.md) | `fun getBlockHardness(blockState: BlockState, worldIn: IBlockReader, pos: BlockPos): `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [hasTileEntity](has-tile-entity.md) | `fun hasTileEntity(state: BlockState?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onBlockActivated](on-block-activated.md) | `fun onBlockActivated(state: BlockState, worldIn: World, pos: BlockPos, player: PlayerEntity, handIn: Hand, hit: BlockRayTraceResult): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onBlockHarvested](on-block-harvested.md) | `fun onBlockHarvested(worldIn: World, pos: BlockPos, state: BlockState, player: PlayerEntity): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
