[B114](../../index.md) / [ejektaflex.bountiful.ext](../index.md) / [net.minecraft.nbt.CompoundNBT](./index.md)

### Extensions for net.minecraft.nbt.CompoundNBT

| Name | Summary |
|---|---|
| [clear](clear.md) | `fun CompoundNBT.clear(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getUnsortedList](get-unsorted-list.md) | `fun CompoundNBT.getUnsortedList(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<CompoundNBT>` |
| [getUnsortedListTyped](get-unsorted-list-typed.md) | `fun <T : INBTSerializable<CompoundNBT>> CompoundNBT.getUnsortedListTyped(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, fact: () -> T): `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<T>` |
| [setUnsortedList](set-unsorted-list.md) | `fun CompoundNBT.setUnsortedList(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, items: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<INBTSerializable<CompoundNBT>>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setUnsortedListOfNbt](set-unsorted-list-of-nbt.md) | `fun CompoundNBT.setUnsortedListOfNbt(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, items: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<CompoundNBT>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [toBountyEntry](to-bounty-entry.md) | `val CompoundNBT.toBountyEntry: `[`BountyEntry`](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md) |
| [toItemStack](to-item-stack.md) | `val CompoundNBT.toItemStack: ItemStack?` |
