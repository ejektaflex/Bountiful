[B114](../../index.md) / [ejektaflex.bountiful.data.bounty](../index.md) / [IBountyReward](./index.md)

# IBountyReward

`interface IBountyReward` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/bounty/IBountyReward.kt#L6)

### Functions

| Name | Summary |
|---|---|
| [reward](reward.md) | `abstract fun reward(player: PlayerEntity): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [tooltipReward](tooltip-reward.md) | `abstract fun tooltipReward(): ITextComponent` |

### Inheritors

| Name | Summary |
|---|---|
| [AbstractBountyEntryStackLike](../-abstract-bounty-entry-stack-like/index.md) | `abstract class AbstractBountyEntryStackLike : `[`BountyEntry`](../-bounty-entry/index.md)`, `[`IBountyObjective`](../-i-bounty-objective/index.md)`, `[`IBountyReward`](./index.md) |
| [BountyEntryCommand](../-bounty-entry-command/index.md) | `class BountyEntryCommand : `[`BountyEntry`](../-bounty-entry/index.md)`, `[`IBountyReward`](./index.md) |
| [BountyEntryExperience](../-bounty-entry-experience/index.md) | `class BountyEntryExperience : `[`BountyEntry`](../-bounty-entry/index.md)`, `[`IBountyObjective`](../-i-bounty-objective/index.md)`, `[`IBountyReward`](./index.md) |
| [BountyEntryItem](../-bounty-entry-item/index.md) | `class BountyEntryItem : `[`AbstractBountyEntryStackLike`](../-abstract-bounty-entry-stack-like/index.md)`, `[`IBountyObjective`](../-i-bounty-objective/index.md)`, `[`IBountyReward`](./index.md) |
| [BountyEntryItemTag](../-bounty-entry-item-tag/index.md) | `class BountyEntryItemTag : `[`AbstractBountyEntryStackLike`](../-abstract-bounty-entry-stack-like/index.md)`, `[`IBountyObjective`](../-i-bounty-objective/index.md)`, `[`IBountyReward`](./index.md) |
