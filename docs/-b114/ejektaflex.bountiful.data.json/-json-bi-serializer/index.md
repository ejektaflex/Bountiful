[B114](../../index.md) / [ejektaflex.bountiful.data.json](../index.md) / [JsonBiSerializer](./index.md)

# JsonBiSerializer

`interface JsonBiSerializer<T> : JsonDeserializer<T>, JsonSerializer<T>` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/data/json/JsonBiSerializer.kt#L6)

### Functions

| Name | Summary |
|---|---|
| [deserialize](deserialize.md) | `open fun deserialize(json: JsonElement?, typeOfT: `[`Type`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Type.html)`?, context: JsonDeserializationContext?): T` |
| [serialize](serialize.md) | `open fun serialize(src: T, typeOfSrc: `[`Type`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Type.html)`?, context: JsonSerializationContext?): JsonElement` |

### Inheritors

| Name | Summary |
|---|---|
| [BountyEntry](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md) | `abstract class BountyEntry : `[`JsonBiSerializer`](./index.md)`<`[`BountyEntry`](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md)`>, INBTSerializable<CompoundNBT>, `[`IWeighted`](../../ejektaflex.bountiful.util/-i-weighted/index.md)`, `[`Cloneable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-cloneable/index.html) |
