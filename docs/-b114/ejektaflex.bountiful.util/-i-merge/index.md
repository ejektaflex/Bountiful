[B114](../../index.md) / [ejektaflex.bountiful.util](../index.md) / [IMerge](./index.md)

# IMerge

`interface IMerge<T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> : `[`IIdentifiable`](../-i-identifiable/index.md) [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/util/IMerge.kt#L3)

### Properties

| Name | Summary |
|---|---|
| [canLoad](can-load.md) | `abstract val canLoad: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Functions

| Name | Summary |
|---|---|
| [merge](merge.md) | `abstract fun merge(other: T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [Decree](../../ejektaflex.bountiful.data.structure/-decree/index.md) | `data class Decree : INBTSerializable<CompoundNBT>, `[`IMerge`](./index.md)`<`[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`>, `[`IIdentifiable`](../-i-identifiable/index.md) |
| [EntryPool](../../ejektaflex.bountiful.data.structure/-entry-pool/index.md) | `open class EntryPool : `[`ValueRegistry`](../-value-registry/index.md)`<`[`BountyEntry`](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md)`>, `[`IMerge`](./index.md)`<`[`EntryPool`](../../ejektaflex.bountiful.data.structure/-entry-pool/index.md)`>, `[`IIdentifiable`](../-i-identifiable/index.md) |
