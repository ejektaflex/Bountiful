[B114](../index.md) / [ejektaflex.bountiful.data.bounty.checkers](./index.md)

## Package ejektaflex.bountiful.data.bounty.checkers

### Types

| Name | Summary |
|---|---|
| [CheckerRegistry](-checker-registry/index.md) | `object CheckerRegistry : `[`ValueRegistry`](../ejektaflex.bountiful.util/-value-registry/index.md)`<`[`KClass`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)`<out `[`CheckHandler`](-check-handler/index.md)`<*>>>` |
| [CheckHandler](-check-handler/index.md) | `abstract class CheckHandler<T : `[`BountyEntry`](../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md)`>` |
| [EntityCheckHandler](-entity-check-handler/index.md) | `class EntityCheckHandler : `[`CheckHandler`](-check-handler/index.md)`<`[`BountyEntryEntity`](../ejektaflex.bountiful.data.bounty/-bounty-entry-entity/index.md)`>` |
| [ExperienceCheckHandler](-experience-check-handler/index.md) | `class ExperienceCheckHandler : `[`CheckHandler`](-check-handler/index.md)`<`[`BountyEntryEntity`](../ejektaflex.bountiful.data.bounty/-bounty-entry-entity/index.md)`>` |
| [StackLikeCheckHandler](-stack-like-check-handler/index.md) | `class StackLikeCheckHandler : `[`CheckHandler`](-check-handler/index.md)`<`[`BountyEntryItem`](../ejektaflex.bountiful.data.bounty/-bounty-entry-item/index.md)`>` |
| [StackPartition](-stack-partition/index.md) | `class StackPartition` |
