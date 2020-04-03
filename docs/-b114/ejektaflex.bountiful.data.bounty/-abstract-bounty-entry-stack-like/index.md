[B114](../../index.md) / [ejektaflex.bountiful.data.bounty](../index.md) / [AbstractBountyEntryStackLike](./index.md)

# AbstractBountyEntryStackLike

`abstract class AbstractBountyEntryStackLike : `[`BountyEntry`](../-bounty-entry/index.md)`, `[`IBountyObjective`](../-i-bounty-objective/index.md)`, `[`IBountyReward`](../-i-bounty-reward/index.md) [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/bounty/AbstractBountyEntryStackLike.kt#L8)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `AbstractBountyEntryStackLike()` |

### Properties

| Name | Summary |
|---|---|
| [calculatedWorth](calculated-worth.md) | `open val calculatedWorth: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [validStacks](valid-stacks.md) | `abstract val validStacks: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<ItemStack>` |

### Functions

| Name | Summary |
|---|---|
| [tooltipObjective](tooltip-objective.md) | `open fun tooltipObjective(progress: `[`BountyProgress`](../-bounty-progress/index.md)`): ITextComponent` |
| [tooltipReward](tooltip-reward.md) | `open fun tooltipReward(): ITextComponent` |
| [toString](to-string.md) | `open fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [BountyEntryItem](../-bounty-entry-item/index.md) | `class BountyEntryItem : `[`AbstractBountyEntryStackLike`](./index.md)`, `[`IBountyObjective`](../-i-bounty-objective/index.md)`, `[`IBountyReward`](../-i-bounty-reward/index.md) |
| [BountyEntryItemTag](../-bounty-entry-item-tag/index.md) | `class BountyEntryItemTag : `[`AbstractBountyEntryStackLike`](./index.md)`, `[`IBountyObjective`](../-i-bounty-objective/index.md)`, `[`IBountyReward`](../-i-bounty-reward/index.md) |
