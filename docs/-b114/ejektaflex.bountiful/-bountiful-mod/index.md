[B114](../../index.md) / [ejektaflex.bountiful](../index.md) / [BountifulMod](./index.md)

# BountifulMod

`object BountifulMod` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/BountifulMod.kt#L25)

### Types

| Name | Summary |
|---|---|
| [BountifulResource](-bountiful-resource/index.md) | `data class BountifulResource` |

### Properties

| Name | Summary |
|---|---|
| [config](config.md) | `val config: `[`BountifulConfig`](../-bountiful-config/index.md) |
| [logFile](log-file.md) | `val logFile: `[`File`](https://docs.oracle.com/javase/8/docs/api/java/io/File.html) |
| [logFolder](log-folder.md) | `val logFolder: `[`File`](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)`!` |
| [logger](logger.md) | `val logger: Logger` |
| [MODID](-m-o-d-i-d.md) | `const val MODID: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [VERSION](-v-e-r-s-i-o-n.md) | `const val VERSION: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [loadResource](load-resource.md) | `fun loadResource(manager: IResourceManager, msgSender: CommandSource?, location: BountifulResource, fillType: `[`BountifulResourceType`](../../ejektaflex.bountiful.data.bounty.enums/-bountiful-resource-type/index.md)`): `[`IMerge`](../../ejektaflex.bountiful.util/-i-merge/index.md)`<`[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>?` |
| [reloadBountyData](reload-bounty-data.md) | `fun reloadBountyData(server: MinecraftServer, manager: IResourceManager = server.resourceManager, fillType: `[`BountifulResourceType`](../../ejektaflex.bountiful.data.bounty.enums/-bountiful-resource-type/index.md)`, msgSender: CommandSource? = null): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [rlFileName](rl-file-name.md) | `fun rlFileName(rl: ResourceLocation): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [rlFileNameNoExt](rl-file-name-no-ext.md) | `fun rlFileNameNoExt(rl: ResourceLocation): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
