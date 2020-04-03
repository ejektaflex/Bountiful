[B114](../../index.md) / [ejektaflex.bountiful.util](../index.md) / [ValueRegistry](./index.md)

# ValueRegistry

`open class ValueRegistry<T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/util/ValueRegistry.kt#L5)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ValueRegistry()` |

### Properties

| Name | Summary |
|---|---|
| [content](content.md) | `val content: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<T>` |

### Functions

| Name | Summary |
|---|---|
| [add](add.md) | `fun add(vararg items: T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [backup](backup.md) | `fun backup(): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>` |
| [empty](empty.md) | `fun empty(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [iterator](iterator.md) | `operator fun iterator(): `[`MutableIterator`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-iterator/index.html)`<T>` |
| [remove](remove.md) | `fun remove(item: T): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [replace](replace.md) | `fun replace(newItems: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>, condition: T.() -> `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>` |
| [restore](restore.md) | `fun restore(backupList: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [CheckerRegistry](../../ejektaflex.bountiful.data.bounty.checkers/-checker-registry/index.md) | `object CheckerRegistry : `[`ValueRegistry`](./index.md)`<`[`KClass`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)`<out `[`CheckHandler`](../../ejektaflex.bountiful.data.bounty.checkers/-check-handler/index.md)`<*>>>` |
| [DecreeRegistry](../../ejektaflex.bountiful.data.registry/-decree-registry/index.md) | `object DecreeRegistry : `[`ValueRegistry`](./index.md)`<`[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`>` |
| [EntryPool](../../ejektaflex.bountiful.data.structure/-entry-pool/index.md) | `open class EntryPool : `[`ValueRegistry`](./index.md)`<`[`BountyEntry`](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md)`>, `[`IMerge`](../-i-merge/index.md)`<`[`EntryPool`](../../ejektaflex.bountiful.data.structure/-entry-pool/index.md)`>, `[`IIdentifiable`](../-i-identifiable/index.md) |
| [PoolRegistry](../../ejektaflex.bountiful.data.registry/-pool-registry/index.md) | `object PoolRegistry : `[`ValueRegistry`](./index.md)`<`[`EntryPool`](../../ejektaflex.bountiful.data.structure/-entry-pool/index.md)`>` |
