[B114](../../index.md) / [ejektaflex.bountiful.data.bounty](../index.md) / [BountyEntryEntity](./index.md)

# BountyEntryEntity

`class BountyEntryEntity : `[`BountyEntry`](../-bounty-entry/index.md)`, `[`IBountyObjective`](../-i-bounty-objective/index.md) [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/bounty/BountyEntryEntity.kt#L14)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BountyEntryEntity()` |

### Properties

| Name | Summary |
|---|---|
| [bType](b-type.md) | `var bType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [calculatedWorth](calculated-worth.md) | `val calculatedWorth: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [formattedName](formatted-name.md) | `val formattedName: ITextComponent` |
| [killedAmount](killed-amount.md) | `var killedAmount: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [deserializeNBT](deserialize-n-b-t.md) | `fun deserializeNBT(tag: CompoundNBT): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [isSameEntity](is-same-entity.md) | `fun isSameEntity(e: LivingEntity): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [serializeNBT](serialize-n-b-t.md) | `fun serializeNBT(): CompoundNBT` |
| [tooltipObjective](tooltip-objective.md) | `fun tooltipObjective(progress: `[`BountyProgress`](../-bounty-progress/index.md)`): ITextComponent` |
| [toString](to-string.md) | `fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [validate](validate.md) | `fun validate(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
