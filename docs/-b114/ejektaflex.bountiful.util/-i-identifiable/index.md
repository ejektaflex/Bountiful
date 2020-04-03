[B114](../../index.md) / [ejektaflex.bountiful.util](../index.md) / [IIdentifiable](./index.md)

# IIdentifiable

`interface IIdentifiable` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/util/IIdentifiable.kt#L3)

### Properties

| Name | Summary |
|---|---|
| [id](id.md) | `abstract var id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [Decree](../../ejektaflex.bountiful.data.structure/-decree/index.md) | `data class Decree : INBTSerializable<CompoundNBT>, `[`IMerge`](../-i-merge/index.md)`<`[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`>, `[`IIdentifiable`](./index.md) |
| [EntryPool](../../ejektaflex.bountiful.data.structure/-entry-pool/index.md) | `open class EntryPool : `[`ValueRegistry`](../-value-registry/index.md)`<`[`BountyEntry`](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md)`>, `[`IMerge`](../-i-merge/index.md)`<`[`EntryPool`](../../ejektaflex.bountiful.data.structure/-entry-pool/index.md)`>, `[`IIdentifiable`](./index.md) |
| [IMerge](../-i-merge/index.md) | `interface IMerge<T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> : `[`IIdentifiable`](./index.md) |
