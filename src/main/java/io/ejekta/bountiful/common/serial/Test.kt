@file:UseSerializers(IdentitySer::class)
package io.ejekta.bountiful.common.serial

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.util.Identifier

@Serializable
data class Test(val id: Identifier) {

}