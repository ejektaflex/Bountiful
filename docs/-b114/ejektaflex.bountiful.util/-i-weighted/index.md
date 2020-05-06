[B114](../../index.md) / [ejektaflex.bountiful.util](../index.md) / [IWeighted](./index.md)

# IWeighted

`interface IWeighted` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/util/IWeighted.kt#L7)

### Properties

| Name | Summary |
|---|---|
| [weight](weight.md) | `abstract var weight: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [normalizedWeight](normalized-weight.md) | `open fun normalizedWeight(exp: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [BountyEntry](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md) | `abstract class BountyEntry : `[`JsonBiSerializer`](../../ejektaflex.bountiful.data.json/-json-bi-serializer/index.md)`<`[`BountyEntry`](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md)`>, INBTSerializable<CompoundNBT>, `[`IWeighted`](./index.md)`, `[`Cloneable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-cloneable/index.html) |
