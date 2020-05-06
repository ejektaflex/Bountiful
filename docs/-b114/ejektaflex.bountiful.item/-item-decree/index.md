[B114](../../index.md) / [ejektaflex.bountiful.item](../index.md) / [ItemDecree](./index.md)

# ItemDecree

`class ItemDecree : Item` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/item/ItemDecree.kt#L27)

A Decree [Item](#) in game.

### Exceptions

| Name | Summary |
|---|---|
| [DecreeCreationException](-decree-creation-exception/index.md) | Thrown when bounty NBT data could not be created`class DecreeCreationException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | A Decree [Item](#) in game.`ItemDecree()` |

### Functions

| Name | Summary |
|---|---|
| [addInformation](add-information.md) | `fun addInformation(stack: ItemStack, worldIn: World?, tooltip: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<ITextComponent>, flagIn: ITooltipFlag): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [ensureDecree](ensure-decree.md) | `fun ensureDecree(stack: ItemStack, defaultData: `[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`? = null): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getDisplayName](get-display-name.md) | `fun getDisplayName(stack: ItemStack): ITextComponent` |
| [getTranslationKey](get-translation-key.md) | `fun getTranslationKey(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [setData](set-data.md) | `fun setData(stack: ItemStack, list: `[`DecreeList`](../../ejektaflex.bountiful.data.structure/-decree-list/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [combine](combine.md) | Attempts to combine two Decree items into a new one with both Decree IDs.`fun combine(stackA: ItemStack, stackB: ItemStack): ItemStack?` |
| [makeRandomStack](make-random-stack.md) | Creates a new Decree [ItemStack](#) with a random Decree for data, or null if the Decree Registry is empty.`fun makeRandomStack(): ItemStack?` |
| [makeStack](make-stack.md) | Creates a new Decree [ItemStack](#) and assigns it a random valid Decree.`fun makeStack(): ItemStack`<br>Creates a new Decree [ItemStack](#) with specific Decree data.`fun makeStack(decree: `[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`): ItemStack`<br>Attempts to create a new Decree [ItemStack](#) with the given Decree ID, or returns null if no Decree exists with that ID.`fun makeStack(decId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): ItemStack?` |
