[B114](../../index.md) / [ejektaflex.bountiful.data.bounty.checkers](../index.md) / [CheckHandler](./index.md)

# CheckHandler

`abstract class CheckHandler<T : `[`BountyEntry`](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md)`>` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/bounty/checkers/CheckHandler.kt#L10)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `CheckHandler(inPlayer: PlayerEntity, inData: `[`BountyData`](../../ejektaflex.bountiful.data.bounty/-bounty-data/index.md)`)`<br>`CheckHandler()` |

### Properties

| Name | Summary |
|---|---|
| [data](data.md) | `lateinit var data: `[`BountyData`](../../ejektaflex.bountiful.data.bounty/-bounty-data/index.md) |
| [inv](inv.md) | `lateinit var inv: NonNullList<ItemStack>` |
| [isComplete](is-complete.md) | `val isComplete: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [player](player.md) | `lateinit var player: PlayerEntity` |

### Functions

| Name | Summary |
|---|---|
| [fulfill](fulfill.md) | `abstract fun fulfill(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [initialize](initialize.md) | `fun initialize(inPlayer: PlayerEntity, inData: `[`BountyData`](../../ejektaflex.bountiful.data.bounty/-bounty-data/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [objectiveStatus](objective-status.md) | `abstract fun objectiveStatus(): `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`BountyEntry`](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md)`, `[`BountyProgress`](../../ejektaflex.bountiful.data.bounty/-bounty-progress/index.md)`>` |

### Inheritors

| Name | Summary |
|---|---|
| [EntityCheckHandler](../-entity-check-handler/index.md) | `class EntityCheckHandler : `[`CheckHandler`](./index.md)`<`[`BountyEntryEntity`](../../ejektaflex.bountiful.data.bounty/-bounty-entry-entity/index.md)`>` |
| [ExperienceCheckHandler](../-experience-check-handler/index.md) | `class ExperienceCheckHandler : `[`CheckHandler`](./index.md)`<`[`BountyEntryEntity`](../../ejektaflex.bountiful.data.bounty/-bounty-entry-entity/index.md)`>` |
| [StackLikeCheckHandler](../-stack-like-check-handler/index.md) | `class StackLikeCheckHandler : `[`CheckHandler`](./index.md)`<`[`BountyEntryItem`](../../ejektaflex.bountiful.data.bounty/-bounty-entry-item/index.md)`>` |
