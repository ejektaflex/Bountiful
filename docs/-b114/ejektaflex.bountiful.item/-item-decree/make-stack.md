[B114](../../index.md) / [ejektaflex.bountiful.item](../index.md) / [ItemDecree](index.md) / [makeStack](./make-stack.md)

# makeStack

`fun makeStack(): ItemStack` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/item/ItemDecree.kt#L156)

Creates a new Decree [ItemStack](#) and assigns it a random valid Decree.

**Return**
The new Decree [ItemStack](#)

`fun makeStack(decree: `[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`): ItemStack` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/item/ItemDecree.kt#L167)

Creates a new Decree [ItemStack](#) with specific Decree data.

**Return**
The new Decree [ItemStack](#)

`fun makeStack(decId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): ItemStack?` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/item/ItemDecree.kt#L179)

Attempts to create a new Decree [ItemStack](#) with the given Decree ID,
or returns null if no Decree exists with that ID.

**Return**
The new Decree [ItemStack](#)

