[B114](../../index.md) / [ejektaflex.bountiful.advancement](../index.md) / [BountifulTrigger](./index.md)

# BountifulTrigger

`class BountifulTrigger : ICriterionTrigger<CriterionInstance>` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/advancement/BountifulTrigger.kt#L10)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BountifulTrigger(resId: ResourceLocation)` |

### Properties

| Name | Summary |
|---|---|
| [listeners](listeners.md) | `var listeners: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<PlayerAdvancements, `[`MutableSet`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/index.html)`<Listener<CriterionInstance>>?>` |
| [resId](res-id.md) | `val resId: ResourceLocation` |

### Functions

| Name | Summary |
|---|---|
| [addListener](add-listener.md) | `fun addListener(adv: PlayerAdvancements, listener: Listener<CriterionInstance>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [deserializeInstance](deserialize-instance.md) | `fun deserializeInstance(json: JsonObject, context: JsonDeserializationContext): CriterionInstance` |
| [getId](get-id.md) | `fun getId(): ResourceLocation` |
| [removeAllListeners](remove-all-listeners.md) | `fun removeAllListeners(adv: PlayerAdvancements): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [removeListener](remove-listener.md) | `fun removeListener(adv: PlayerAdvancements, listener: Listener<CriterionInstance>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [trigger](trigger.md) | `fun trigger(adv: PlayerAdvancements): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
