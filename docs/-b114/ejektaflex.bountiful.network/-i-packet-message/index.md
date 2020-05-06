[B114](../../index.md) / [ejektaflex.bountiful.network](../index.md) / [IPacketMessage](./index.md)

# IPacketMessage

`interface IPacketMessage` [(source)](https://github.com/ejektaflex/Bountiful/tree/develop/src/main/kotlin/ejektaflex/bountiful/network/IPacketMessage.kt#L7)

### Functions

| Name | Summary |
|---|---|
| [decode](decode.md) | `abstract fun decode(buff: PacketBuffer): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [encode](encode.md) | `abstract fun encode(buff: PacketBuffer): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [execute](execute.md) | `abstract fun execute(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [handle](handle.md) | Handles the given packet message. By default, calls [execute](execute.md) on the main thread.`open fun handle(ctx: `[`Supplier`](https://docs.oracle.com/javase/8/docs/api/java/util/function/Supplier.html)`<Context>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [MessageClipboardCopy](../-message-clipboard-copy/index.md) | `class MessageClipboardCopy : `[`IPacketMessage`](./index.md) |
