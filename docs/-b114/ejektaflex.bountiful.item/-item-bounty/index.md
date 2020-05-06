[B114](../../index.md) / [ejektaflex.bountiful.item](../index.md) / [ItemBounty](./index.md)

# ItemBounty

`class ItemBounty : Item, IForgeRegistryEntry<Item>` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/item/ItemBounty.kt#L34)

### Exceptions

| Name | Summary |
|---|---|
| [BountyCreationException](-bounty-creation-exception/index.md) | Thrown when bounty NBT data could not be created`class BountyCreationException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ItemBounty()` |

### Functions

| Name | Summary |
|---|---|
| [addInformation](add-information.md) | `fun addInformation(stack: ItemStack, worldIn: World?, tooltip: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<ITextComponent>, flagIn: ITooltipFlag): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [cashIn](cash-in.md) | `fun cashIn(player: PlayerEntity, hand: Hand): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [ensureBounty](ensure-bounty.md) | `fun ensureBounty(stack: ItemStack, worldIn: World, decrees: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`>, rarity: `[`BountyRarity`](../../ejektaflex.bountiful.data.bounty.enums/-bounty-rarity/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [ensureTimerStarted](ensure-timer-started.md) | `fun ensureTimerStarted(stack: ItemStack, worldIn: World): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getDisplayName](get-display-name.md) | `fun getDisplayName(stack: ItemStack): ITextComponent` |
| [getRarity](get-rarity.md) | `fun getRarity(stack: ItemStack): Rarity` |
| [getTranslationKey](get-translation-key.md) | `fun getTranslationKey(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [inventoryTick](inventory-tick.md) | `fun inventoryTick(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, isSelected: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onItemRightClick](on-item-right-click.md) | `fun onItemRightClick(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack>` |
| [shouldCauseReequipAnimation](should-cause-reequip-animation.md) | `fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack, slotChanged: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [calcRarity](calc-rarity.md) | `fun calcRarity(): `[`BountyRarity`](../../ejektaflex.bountiful.data.bounty.enums/-bounty-rarity/index.md) |
| [create](create.md) | `fun create(world: World, decrees: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Decree`](../../ejektaflex.bountiful.data.structure/-decree/index.md)`>): ItemStack` |
