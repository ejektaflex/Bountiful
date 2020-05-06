[B114](../../index.md) / [ejektaflex.bountiful.data.structure](../index.md) / [Decree](./index.md)

# Decree

`data class Decree : INBTSerializable<CompoundNBT>, `[`IMerge`](../../ejektaflex.bountiful.util/-i-merge/index.md)`<`[`Decree`](./index.md)`>, `[`IIdentifiable`](../../ejektaflex.bountiful.util/-i-identifiable/index.md) [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/structure/Decree.kt#L9)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Decree(spawnsInBoard: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, objectivePools: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = mutableListOf(), rewardPools: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = mutableListOf())` |

### Properties

| Name | Summary |
|---|---|
| [canLoad](can-load.md) | `val canLoad: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [id](id.md) | `var id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [objectivePools](objective-pools.md) | `var objectivePools: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [rewardPools](reward-pools.md) | `var rewardPools: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [spawnsInBoard](spawns-in-board.md) | `var spawnsInBoard: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Functions

| Name | Summary |
|---|---|
| [deserializeNBT](deserialize-n-b-t.md) | `fun deserializeNBT(nbt: CompoundNBT?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [merge](merge.md) | `fun merge(other: `[`Decree`](./index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [serializeNBT](serialize-n-b-t.md) | `fun serializeNBT(): CompoundNBT` |

### Companion Object Properties

| Name | Summary |
|---|---|
| [INVALID](-i-n-v-a-l-i-d.md) | `val INVALID: `[`Decree`](./index.md) |
