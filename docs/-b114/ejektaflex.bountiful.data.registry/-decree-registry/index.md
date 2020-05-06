[B114](../../index.md) / [ejektaflex.bountiful.data.registry](../index.md) / [DecreeRegistry](./index.md)

# DecreeRegistry

`object DecreeRegistry : `[`ValueRegistry`](../../ejektaflex.bountiful.util/-value-registry/index.md)`<`[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`>` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/registry/DecreeRegistry.kt#L10)

### Properties

| Name | Summary |
|---|---|
| [ids](ids.md) | `val ids: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |

### Functions

| Name | Summary |
|---|---|
| [getDecree](get-decree.md) | `fun getDecree(id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`?` |
| [getObjectives](get-objectives.md) | `fun getObjectives(decrees: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`>): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BountyEntry`](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md)`>` |
| [getRewards](get-rewards.md) | `fun getRewards(decrees: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`>): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BountyEntry`](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md)`>` |
