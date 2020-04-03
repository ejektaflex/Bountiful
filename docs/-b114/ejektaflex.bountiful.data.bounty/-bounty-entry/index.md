[B114](../../index.md) / [ejektaflex.bountiful.data.bounty](../index.md) / [BountyEntry](./index.md)

# BountyEntry

`abstract class BountyEntry : `[`JsonBiSerializer`](../../ejektaflex.bountiful.data.json/-json-bi-serializer/index.md)`<`[`BountyEntry`](./index.md)`>, INBTSerializable<CompoundNBT>, `[`IWeighted`](../../ejektaflex.bountiful.util/-i-weighted/index.md)`, `[`Cloneable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-cloneable/index.html) [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/bounty/BountyEntry.kt#L21)

### Exceptions

| Name | Summary |
|---|---|
| [EntryValidationException](-entry-validation-exception/index.md) | `class EntryValidationException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BountyEntry()` |

### Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | `var amount: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [amountRange](amount-range.md) | `open var amountRange: `[`ItemRange`](../../ejektaflex.bountiful.util/-item-range/index.md) |
| [bType](b-type.md) | `open var bType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [calculatedWorth](calculated-worth.md) | `abstract val calculatedWorth: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [content](content.md) | `open var content: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [formattedName](formatted-name.md) | `abstract val formattedName: ITextComponent` |
| [jsonNBT](json-n-b-t.md) | `var jsonNBT: JsonElement?` |
| [maxWorth](max-worth.md) | `val maxWorth: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [minWorth](min-worth.md) | `val minWorth: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [name](name.md) | `open var name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [nbtTag](nbt-tag.md) | `val nbtTag: CompoundNBT?` |
| [randCount](rand-count.md) | `val randCount: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [timeMult](time-mult.md) | `open var timeMult: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`?` |
| [unitWorth](unit-worth.md) | `open var unitWorth: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [weight](weight.md) | `open var weight: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [cloned](cloned.md) | `fun cloned(): `[`BountyEntry`](./index.md) |
| [deserializeNBT](deserialize-n-b-t.md) | `open fun deserializeNBT(tag: CompoundNBT): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [pick](pick.md) | `open fun pick(worth: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`? = null): `[`BountyEntry`](./index.md) |
| [serializeNBT](serialize-n-b-t.md) | `open fun serializeNBT(): CompoundNBT` |
| [validate](validate.md) | `open fun validate(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [worthDistanceFrom](worth-distance-from.md) | `fun worthDistanceFrom(value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [AbstractBountyEntryStackLike](../-abstract-bounty-entry-stack-like/index.md) | `abstract class AbstractBountyEntryStackLike : `[`BountyEntry`](./index.md)`, `[`IBountyObjective`](../-i-bounty-objective/index.md)`, `[`IBountyReward`](../-i-bounty-reward/index.md) |
| [BountyEntryCommand](../-bounty-entry-command/index.md) | `class BountyEntryCommand : `[`BountyEntry`](./index.md)`, `[`IBountyReward`](../-i-bounty-reward/index.md) |
| [BountyEntryEntity](../-bounty-entry-entity/index.md) | `class BountyEntryEntity : `[`BountyEntry`](./index.md)`, `[`IBountyObjective`](../-i-bounty-objective/index.md) |
| [BountyEntryExperience](../-bounty-entry-experience/index.md) | `class BountyEntryExperience : `[`BountyEntry`](./index.md)`, `[`IBountyObjective`](../-i-bounty-objective/index.md)`, `[`IBountyReward`](../-i-bounty-reward/index.md) |
