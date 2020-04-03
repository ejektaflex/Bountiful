[B114](../../index.md) / [ejektaflex.bountiful.data.structure](../index.md) / [EntryPool](./index.md)

# EntryPool

`open class EntryPool : `[`ValueRegistry`](../../ejektaflex.bountiful.util/-value-registry/index.md)`<`[`BountyEntry`](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md)`>, `[`IMerge`](../../ejektaflex.bountiful.util/-i-merge/index.md)`<`[`EntryPool`](./index.md)`>, `[`IIdentifiable`](../../ejektaflex.bountiful.util/-i-identifiable/index.md) [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/structure/EntryPool.kt#L12)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `EntryPool()` |

### Properties

| Name | Summary |
|---|---|
| [canLoad](can-load.md) | `open val canLoad: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [id](id.md) | `open var id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [modsRequired](mods-required.md) | `var modsRequired: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>?` |

### Functions

| Name | Summary |
|---|---|
| [merge](merge.md) | `open fun merge(other: `[`EntryPool`](./index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
