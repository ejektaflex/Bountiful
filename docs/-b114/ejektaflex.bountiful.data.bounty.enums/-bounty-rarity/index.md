[B114](../../index.md) / [ejektaflex.bountiful.data.bounty.enums](../index.md) / [BountyRarity](./index.md)

# BountyRarity

`enum class BountyRarity` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/bounty/enums/BountyRarity.kt#L9)

### Enum Values

| Name | Summary |
|---|---|
| [Common](-common.md) |  |
| [Uncommon](-uncommon.md) |  |
| [Rare](-rare.md) |  |
| [Epic](-epic.md) |  |

### Properties

| Name | Summary |
|---|---|
| [exponent](exponent.md) | `val exponent: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [extraRewardChance](extra-reward-chance.md) | `val extraRewardChance: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [itemRarity](item-rarity.md) | `val itemRarity: Rarity` |
| [stat](stat.md) | `val stat: ResourceLocation` |
| [worthMult](worth-mult.md) | `val worthMult: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |

### Functions

| Name | Summary |
|---|---|
| [trigger](trigger.md) | `fun trigger(playerEntity: ServerPlayerEntity): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [getRarityFromInt](get-rarity-from-int.md) | `fun getRarityFromInt(n: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`BountyRarity`](./index.md) |
| [tryTriggerAll](try-trigger-all.md) | `fun tryTriggerAll(player: ServerPlayerEntity): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
