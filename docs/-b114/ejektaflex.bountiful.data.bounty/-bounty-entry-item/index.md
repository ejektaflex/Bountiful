[B114](../../index.md) / [ejektaflex.bountiful.data.bounty](../index.md) / [BountyEntryItem](./index.md)

# BountyEntryItem

`class BountyEntryItem : `[`AbstractBountyEntryStackLike`](../-abstract-bounty-entry-stack-like/index.md)`, `[`IBountyObjective`](../-i-bounty-objective/index.md)`, `[`IBountyReward`](../-i-bounty-reward/index.md) [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/bounty/BountyEntryItem.kt#L15)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BountyEntryItem()` |

### Properties

| Name | Summary |
|---|---|
| [bType](b-type.md) | `var bType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [formattedName](formatted-name.md) | `val formattedName: ITextComponent` |
| [itemStack](item-stack.md) | `val itemStack: ItemStack?` |
| [validStacks](valid-stacks.md) | `val validStacks: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<ItemStack>` |

### Functions

| Name | Summary |
|---|---|
| [reward](reward.md) | `fun reward(player: PlayerEntity): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [validate](validate.md) | `fun validate(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
