[B114](../../index.md) / [ejektaflex.bountiful.data.bounty](../index.md) / [BountyEntryExperience](./index.md)

# BountyEntryExperience

`class BountyEntryExperience : `[`BountyEntry`](../-bounty-entry/index.md)`, `[`IBountyObjective`](../-i-bounty-objective/index.md)`, `[`IBountyReward`](../-i-bounty-reward/index.md) [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/bounty/BountyEntryExperience.kt#L15)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BountyEntryExperience()` |

### Properties

| Name | Summary |
|---|---|
| [bType](b-type.md) | `var bType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [calculatedWorth](calculated-worth.md) | `val calculatedWorth: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [formattedName](formatted-name.md) | `val formattedName: ITextComponent` |

### Functions

| Name | Summary |
|---|---|
| [reward](reward.md) | `fun reward(player: PlayerEntity): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [tooltipObjective](tooltip-objective.md) | `fun tooltipObjective(progress: `[`BountyProgress`](../-bounty-progress/index.md)`): ITextComponent` |
| [tooltipReward](tooltip-reward.md) | `fun tooltipReward(): ITextComponent` |
