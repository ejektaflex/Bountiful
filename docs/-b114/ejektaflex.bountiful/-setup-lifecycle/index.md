[B114](../../index.md) / [ejektaflex.bountiful](../index.md) / [SetupLifecycle](./index.md)

# SetupLifecycle

`object SetupLifecycle` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/SetupLifecycle.kt#L52)

### Functions

| Name | Summary |
|---|---|
| [anvilEvent](anvil-event.md) | `fun anvilEvent(event: AnvilUpdateEvent): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [doVillagerTrades](do-villager-trades.md) | `fun doVillagerTrades(event: VillagerTradesEvent): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [doWandererTrades](do-wanderer-trades.md) | `fun doWandererTrades(event: WandererTradesEvent): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [entityLivingDeath](entity-living-death.md) | `fun entityLivingDeath(e: LivingDeathEvent): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [gameSetup](game-setup.md) | `fun gameSetup(event: FMLCommonSetupEvent): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [jeiConfig](jei-config.md) | `fun jeiConfig(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onClientInit](on-client-init.md) | `fun onClientInit(event: FMLClientSetupEvent): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onContainerRegistry](on-container-registry.md) | `fun onContainerRegistry(event: Register<ContainerType<*>>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onServerAboutToStart](on-server-about-to-start.md) | `fun onServerAboutToStart(event: FMLServerAboutToStartEvent): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onServerStarting](on-server-starting.md) | `fun onServerStarting(event: FMLServerStartingEvent): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onTileEntityRegistry](on-tile-entity-registry.md) | `fun onTileEntityRegistry(event: Register<TileEntityType<*>>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [registerBlocks](register-blocks.md) | `fun registerBlocks(event: Register<Block>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [registerItems](register-items.md) | `fun registerItems(event: Register<Item>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [validatePool](validate-pool.md) | `fun validatePool(pool: `[`EntryPool`](../../ejektaflex.bountiful.data.structure/-entry-pool/index.md)`, sender: CommandSource? = null, log: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`BountyEntry`](../../ejektaflex.bountiful.data.bounty/-bounty-entry/index.md)`>` |
