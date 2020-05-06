[B114](../../index.md) / [ejektaflex.bountiful.data.bounty](../index.md) / [BountyData](./index.md)

# BountyData

`class BountyData : INBTSerializable<CompoundNBT>` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/bounty/BountyData.kt#L26)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BountyData()` |

### Properties

| Name | Summary |
|---|---|
| [boardStamp](board-stamp.md) | `var boardStamp: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [bountyStamp](bounty-stamp.md) | `var bountyStamp: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [bountyTime](bounty-time.md) | `var bountyTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [objectives](objectives.md) | `val objectives: `[`ValueRegistry`](../../ejektaflex.bountiful.util/-value-registry/index.md)`<`[`BountyEntry`](../-bounty-entry/index.md)`>` |
| [rarity](rarity.md) | `var rarity: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [rarityEnum](rarity-enum.md) | `val rarityEnum: `[`BountyRarity`](../../ejektaflex.bountiful.data.bounty.enums/-bounty-rarity/index.md) |
| [rewards](rewards.md) | `val rewards: `[`ValueRegistry`](../../ejektaflex.bountiful.util/-value-registry/index.md)`<`[`BountyEntry`](../-bounty-entry/index.md)`>` |

### Functions

| Name | Summary |
|---|---|
| [boardTimeLeft](board-time-left.md) | `fun boardTimeLeft(world: World): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [deserializeNBT](deserialize-n-b-t.md) | `fun deserializeNBT(tag: CompoundNBT): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hasExpired](has-expired.md) | `fun hasExpired(world: World): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [remainingTime](remaining-time.md) | `fun remainingTime(world: World): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [serializeNBT](serialize-n-b-t.md) | `fun serializeNBT(): CompoundNBT` |
| [timeLeft](time-left.md) | `fun timeLeft(world: World): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [timeTaken](time-taken.md) | `fun timeTaken(world: World): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [tooltipInfo](tooltip-info.md) | `fun tooltipInfo(world: World, advanced: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<ITextComponent>` |

### Companion Object Properties

| Name | Summary |
|---|---|
| [boardTickFreq](board-tick-freq.md) | `const val boardTickFreq: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [bountyTickFreq](bounty-tick-freq.md) | `const val bountyTickFreq: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [maxTimeAtBoard](max-time-at-board.md) | `val maxTimeAtBoard: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [create](create.md) | `fun create(inRarity: `[`BountyRarity`](../../ejektaflex.bountiful.data.bounty.enums/-bounty-rarity/index.md)`, decrees: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`>): `[`BountyData`](./index.md) |
| [createObjectives](create-objectives.md) | `fun createObjectives(rewards: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BountyEntry`](../-bounty-entry/index.md)`>, inRarity: `[`BountyRarity`](../../ejektaflex.bountiful.data.bounty.enums/-bounty-rarity/index.md)`, decrees: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`>, worth: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BountyEntry`](../-bounty-entry/index.md)`>` |
| [createRewards](create-rewards.md) | `fun createRewards(inRarity: `[`BountyRarity`](../../ejektaflex.bountiful.data.bounty.enums/-bounty-rarity/index.md)`, decrees: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`>): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BountyEntry`](../-bounty-entry/index.md)`>` |
| [getObjectivesWithinVariance](get-objectives-within-variance.md) | `fun getObjectivesWithinVariance(objs: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BountyEntry`](../-bounty-entry/index.md)`>, worth: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, variance: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BountyEntry`](../-bounty-entry/index.md)`>` |
| [isValidBounty](is-valid-bounty.md) | `fun isValidBounty(stack: ItemStack): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [pickObjective](pick-objective.md) | `fun pickObjective(objectives: NonNullList<`[`BountyEntry`](../-bounty-entry/index.md)`>, worth: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`BountyEntry`](../-bounty-entry/index.md) |
