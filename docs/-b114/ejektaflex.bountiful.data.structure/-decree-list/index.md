[B114](../../index.md) / [ejektaflex.bountiful.data.structure](../index.md) / [DecreeList](./index.md)

# DecreeList

`class DecreeList : INBTSerializable<CompoundNBT>` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/structure/DecreeList.kt#L8)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `DecreeList()` |

### Properties

| Name | Summary |
|---|---|
| [ids](ids.md) | `var ids: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |

### Functions

| Name | Summary |
|---|---|
| [deserializeNBT](deserialize-n-b-t.md) | `fun deserializeNBT(nbt: CompoundNBT): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [plus](plus.md) | `operator fun plus(other: `[`DecreeList`](./index.md)`): `[`DecreeList`](./index.md) |
| [serializeNBT](serialize-n-b-t.md) | `fun serializeNBT(): CompoundNBT` |
